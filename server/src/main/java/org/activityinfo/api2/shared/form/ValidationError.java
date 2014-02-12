package org.activityinfo.api2.shared.form;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;

/**
 * Validation Error
 */
public class ValidationError {

    private Cuid fieldId;
    private boolean fatal;
    private String message;

    public ValidationError(Cuid fieldId, boolean fatal, String message) {
        this.fieldId = fieldId;
        this.fatal = fatal;
        this.message = message;
    }

    public Cuid getFieldId() {
        return fieldId;
    }

    public boolean isFatal() {
        return fatal;
    }

    public String getMessage() {
        return message;
    }
}
