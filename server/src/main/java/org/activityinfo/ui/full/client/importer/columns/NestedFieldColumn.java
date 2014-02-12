package org.activityinfo.ui.full.client.importer.columns;

import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.draft.DraftFieldValue;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.ValueStatus;

/**
 * A column that is mapped to a nested data field.
 */
public class NestedFieldColumn implements DraftColumn {

    private final FormTree.Node fieldNode;
    private FieldPath path;

    public NestedFieldColumn(FormTree.Node fieldNode) {
        this.fieldNode = fieldNode;
        this.path = fieldNode.getPath();
    }

    @Override
    public LocalizedString getHeader() {
        return new LocalizedString(fieldNode.getParent().getField().getLabel() + " " +
                fieldNode.getField().getLabel());
    }

    @Override
    public Object getValue(DraftInstance instance) {
        DraftFieldValue field = instance.getField(path);
        if(field.getMatchedValue() != null) {
            if(field.getMatchedValue().equals(field.getImportedValue())) {
                return field.getMatchedValue();
            } else {
                return field.getMatchedValue() + " (was: " + field.getImportedValue() + ")";
            }
        }
        return "";
    }

    @Override
    public ValueStatus getStatus(DraftInstance instance) {
        DraftFieldValue field = instance.getField(path);
        if(field.getMatchScore() < 0.90) {
            return ValueStatus.WARNING;
        } else if(field.getMatchScore() < 0.80) {
            return ValueStatus.ERROR;
        } else {
            return ValueStatus.OK;
        }
    }
}
