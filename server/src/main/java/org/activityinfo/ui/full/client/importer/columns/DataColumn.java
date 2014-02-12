package org.activityinfo.ui.full.client.importer.columns;

import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.draft.DraftFieldValue;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.ValueStatus;

/**
 * Column that is mapped to a data field on the root FormClass
 */
public class DataColumn implements DraftColumn {

    private FormTree.Node fieldNode;
    private FieldPath fieldPath;

    public DataColumn(FormTree.Node fieldNode) {
        this.fieldNode = fieldNode;
        this.fieldPath = fieldNode.getPath();
    }

    @Override
    public LocalizedString getHeader() {
        return fieldNode.getField().getLabel();
    }

    @Override
    public Object getValue(DraftInstance instance) {

        DraftFieldValue fieldValue = instance.getField(fieldPath);
        if(fieldValue.isConversionError()) {
            return fieldValue.getImportedValue();
        } else {
            return fieldValue.getMatchedValue();
        }
    }

    @Override
    public ValueStatus getStatus(DraftInstance instance) {
        DraftFieldValue fieldValue = instance.getField(fieldPath);
        if(fieldValue.isConversionError()) {
            return ValueStatus.ERROR;
        } else {
            return ValueStatus.OK;
        }
    }
}
