package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
 * Binding for a field that has not been mapped to a source column.
 * The user can provide a single value that will be used for all {@code SourceRows}
 */
public class MissingFieldBinding implements FieldBinding {

    private final FormTree.Node fieldNode;

    private Cuid providedValue;

    public MissingFieldBinding(FormTree.Node fieldNode) {
        this.fieldNode = fieldNode;
    }

    @Override
    public Cuid getFieldId() {
        return fieldNode.getFieldId();
    }

    @Override
    public FormField getField() {
        return fieldNode.getField();
    }

    public Cuid getProvidedValue() {
        return providedValue;
    }

    public void setProvidedValue(Cuid providedValue) {
        this.providedValue = providedValue;
    }

    @Override
    public Object getFieldValue(SourceRow row) {
        return providedValue;
    }
}
