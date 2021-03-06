package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.type.converter.JsCoordinateNumberFormatter;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.core.shared.type.converter.CoordinateAxis;
import org.activityinfo.core.shared.type.converter.CoordinateFormatException;
import org.activityinfo.core.shared.type.converter.CoordinateParser;
import org.activityinfo.fp.client.Promise;

import java.util.Arrays;
import java.util.List;

/**
 * Importer for Lat/Lng
 */
public class GeographicPointImporter implements FieldImporter {

    private final Cuid fieldId;
    private final ColumnAccessor[] sourceColumns;
    private final CoordinateParser[] coordinateParsers;
    private final List<FieldImporterColumn> fieldImporterColumns;

    public GeographicPointImporter(Cuid fieldId, ColumnAccessor[] sourceColumns, ImportTarget[] targetSites) {
        this.fieldId = fieldId;
        this.sourceColumns = sourceColumns;
        this.coordinateParsers = new CoordinateParser[]{
                new CoordinateParser(CoordinateAxis.LATITUDE, JsCoordinateNumberFormatter.INSTANCE),
                new CoordinateParser(CoordinateAxis.LONGITUDE, JsCoordinateNumberFormatter.INSTANCE)
        };
        this.fieldImporterColumns = Arrays.asList(
                new FieldImporterColumn(targetSites[0], sourceColumns[0]),
                new FieldImporterColumn(targetSites[1], sourceColumns[1]));
    }

    @Override
    public Promise<Void> prepare(ResourceLocator locator, List<SourceRow> batch) {
        return Promise.done();
    }

    @Override
    public List<FieldImporterColumn> getColumns() {
        return fieldImporterColumns;
    }

    @Override
    public void validateInstance(SourceRow row, List<ValidationResult> results) {
        boolean latitudeMissing = sourceColumns[0].isMissing(row);
        boolean longitudeMissing = sourceColumns[1].isMissing(row);

        if (latitudeMissing && longitudeMissing) {
            results.add(ValidationResult.MISSING);
            results.add(ValidationResult.MISSING);
        } else {
            results.add(validateCoordinate(row, 0));
            results.add(validateCoordinate(row, 1));
        }
    }

    private ValidationResult validateCoordinate(SourceRow row, int i) {

        if (sourceColumns[i].isMissing(row)) {
            return ValidationResult.error("Both latitude and longitude are required");
        }

        try {
            double coordinate = parseCoordinate(row, i);
            // we reformat the coordinate make clear the conversion
            return ValidationResult.converted(coordinateParsers[i].format(coordinate), 1);
        } catch (Exception e) {
            return ValidationResult.error(e.getMessage());
        }
    }

    private double parseCoordinate(SourceRow row, int i) throws CoordinateFormatException {
        String string = sourceColumns[i].getValue(row);
        return coordinateParsers[i].parse(string);
    }

    @Override
    public boolean updateInstance(SourceRow row, FormInstance instance) {
        final boolean isLatOk = validateCoordinate(row, 0).shouldPersist();
        final boolean isLonOk = validateCoordinate(row, 1).shouldPersist();
        if (isLatOk && isLonOk) {
            double latitude = parseCoordinate(row, 0);
            double longitude = parseCoordinate(row, 1);
            instance.set(fieldId, new AiLatLng(latitude, longitude));
            return true;
        }
        return false;
    }
}
