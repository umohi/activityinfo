package org.activityinfo.ui.full.client.importer.draft;

import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.converter.Converter;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
 * Copies/converts from a source column to a field in a DraftInstance
 */
public class DraftFieldUpdater {
    private int sourceColumn;
    private FieldPath targetFieldPath;
    private Converter converter;

    public DraftFieldUpdater(int sourceColumn, FieldPath targetFieldPath, Converter converter) {
        this.sourceColumn = sourceColumn;
        this.targetFieldPath = targetFieldPath;
        this.converter = converter;
    }

    public void update(SourceRow sourceRow, DraftInstance instance) {
        DraftFieldValue fieldValue = instance.getField(targetFieldPath);
        fieldValue.setImportedValue(sourceRow.getColumnValue(sourceColumn));
        fieldValue.setConversionError(false);

        Object importedValue = fieldValue.getImportedValue();
        if(importedValue == null) {
            fieldValue.setMatchedValue(null);
        } else {
            try {
                if(importedValue instanceof String) {
                    fieldValue.setMatchedValue(converter.convertString((String) importedValue));
                } else {
                    throw new UnsupportedOperationException();
                }
            } catch(Exception e) {
                fieldValue.setMatchedValue(null);
                fieldValue.setConversionError(true);
            }
        }
    }
}
