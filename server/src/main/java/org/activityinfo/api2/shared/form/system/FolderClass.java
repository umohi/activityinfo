package org.activityinfo.api2.shared.form.system;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Properties;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;

/**
 * Defines a system-level FormClass of folders
 */
public class FolderClass {

    public static final Cuid FORM_CLASS = new Cuid("_folder");

    public static final Cuid LABEL_FIELD_ID = new Cuid("_folder_label");

    public static final Cuid DESCRIPTION_FIELD_ID = new Cuid("_folder_description");

    public static final FormClass get() {

        FormField labelField = new FormField(LABEL_FIELD_ID);
        labelField.setSuperProperty(Properties.LABEL_PROPERTY);

        FormField descriptionField = new FormField(DESCRIPTION_FIELD_ID);
        descriptionField.setSuperProperty(Properties.DESCRIPTION_PROPERTY);

        FormClass formClass = new FormClass(FORM_CLASS);
        formClass.addElement(labelField);
        formClass.addElement(descriptionField);

        return formClass;
    }
}