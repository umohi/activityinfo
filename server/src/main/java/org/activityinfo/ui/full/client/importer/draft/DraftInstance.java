package org.activityinfo.ui.full.client.importer.draft;

import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import java.util.Map;

/**
 * Intermediate structure for rows to be imported; initial
 * matching is complete but some remaining input or validation
 * is required from user.
 */
public class DraftInstance {

    private ImportAction action;
    private boolean valid;
    private final Map<FieldPath, DraftFieldValue> fields = Maps.newHashMap();

    public DraftInstance() {
    }

    public DraftFieldValue getField(FieldPath fieldPath) {
        DraftFieldValue field = fields.get(fieldPath);
        if(field == null) {
            field = new DraftFieldValue();
            fields.put(fieldPath, field);
        }
        return field;
    }

    public ImportAction getAction() {
        return action;
    }

    public void setAction(ImportAction action) {
        this.action = action;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
