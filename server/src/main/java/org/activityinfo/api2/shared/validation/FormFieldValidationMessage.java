package org.activityinfo.api2.shared.validation;

import org.activityinfo.api2.shared.Cuid;

/**
 * Validation Error bound to form field.
 */
public class FormFieldValidationMessage extends ValidationMessage {

    private Cuid fieldId;

    public FormFieldValidationMessage(Cuid fieldId) {
        this.fieldId = fieldId;
    }

    public Cuid getFieldId() {
        return fieldId;
    }
}
