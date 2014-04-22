package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.core.shared.type.converter.Converter;
import org.activityinfo.fp.client.Promise;

import java.util.Collections;
import java.util.List;

/**
 * Imports simple data fields using the supplied converter.
 */
public class DataFieldImporter implements FieldImporter {

    private ColumnAccessor source;
    private ImportTarget target;
    private Converter converter;

    public DataFieldImporter(ColumnAccessor source, ImportTarget target, Converter converter) {
        this.source = source;
        this.target = target;
        this.converter = converter;
    }

    @Override
    public Promise<Void> prepare(ResourceLocator locator, List<SourceRow> batch) {
        return Promise.done();
    }

    @Override
    public void validateInstance(SourceRow row, List<ValidationResult> results) {
        results.add(validate(row));
    }

    private ValidationResult validate(SourceRow row) {
        if(source.isMissing(row)) {
            return ValidationResult.MISSING;
        }
        try {
            Object value = converter.convert(source.getValue(row));
            return ValidationResult.OK;
        } catch(Exception e) {
            return ValidationResult.error(e.getMessage());
        }
    }

    @Override
    public boolean updateInstance(SourceRow row, FormInstance instance) {
        final ValidationResult validateResult = validate(row);
        if (validateResult.getState() == ValidationResult.State.OK) {
            instance.set(target.getFieldId(), converter.convert(source.getValue(row)));
            return true;
        }
        return false;
    }

    @Override
    public List<FieldImporterColumn> getColumns() {
        return Collections.singletonList(new FieldImporterColumn(target, source));
    }
}
