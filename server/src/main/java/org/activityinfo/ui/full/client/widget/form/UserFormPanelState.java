package org.activityinfo.ui.full.client.widget.form;

/**
 * User form panel states.
 *
 * @author YuriyZ
 */
public enum UserFormPanelState {
    /**
     * When BOUND, the form is bound to a FormInstance object. Values from the FormInstance should be displayed
     * in the field widgets, and the user should be able to update the FormInstance with their changes IF their
     * changes are valid. When not BOUND, the user is just there to design the form itself and the widgets should be
     * empty or contain dummy data.
     */
    BOUND("BOUND"),
    /**
     * If DESIGN is enabled, then the user can modify the structure of the form. The user can edit the label and
     * description, change the type of field, reorder fields, and create new form fields and sections
     */
    DESIGN("DESIGN"),
    /**
     * When BOUND and READ_ONLY, the user can view the values of the field, but not modify them.
     */
    READ_ONLY("READ_ONLY");

    private final String value;

    UserFormPanelState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
