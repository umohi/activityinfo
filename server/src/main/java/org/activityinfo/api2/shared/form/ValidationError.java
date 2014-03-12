package org.activityinfo.api2.shared.form;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.validation.ValidationMessage;

/**
 * Validation Error bound to form field.
 */
public class ValidationError extends ValidationMessage {

    private Cuid fieldId;

    public ValidationError(Cuid fieldId) {
        this.fieldId = fieldId;
    }

    public Cuid getFieldId() {
        return fieldId;
    }
}
