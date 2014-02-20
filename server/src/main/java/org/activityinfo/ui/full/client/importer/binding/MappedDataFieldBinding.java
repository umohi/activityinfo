package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.converter.Converter;
import org.activityinfo.ui.full.client.importer.converter.ConverterFactory;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
 * Imports a data (non-reference) field mapped to a single source column
 */
public class MappedDataFieldBinding implements FieldBinding {

    private final FormTree.Node fieldNode;
    private final Converter converter;
    private final int sourceColumn;

    public MappedDataFieldBinding(FormTree.Node fieldNode, int sourceColumn) {
        this.fieldNode = fieldNode;
        this.sourceColumn = sourceColumn;
        this.converter = ConverterFactory.create(fieldNode.getFieldType());
    }

    @Override
    public Cuid getFieldId() {
        return fieldNode.getFieldId();
    }

    @Override
    public FormField getField() {
        return fieldNode.getField();
    }

    public int getSourceColumn() {
        return sourceColumn;
    }

    @Override
    public Object getFieldValue(SourceRow row) {
        String importedValue = getImportedValue(row);
        if(importedValue == null) {
            return null;
        } else {
            try {
                return converter.convertString(importedValue);
            } catch(Exception e) {
                return null;
            }
        }
    }

    public String getImportedValue(SourceRow row) {
        return row.getColumnValue(sourceColumn);
    }

    @Override
    public void accept(FieldBindingColumnVisitor visitor) {
        visitor.visitMappedColumn(this);
    }

    public Object convert(String importedValue) {
        return converter.convertString(importedValue);
    }
}
