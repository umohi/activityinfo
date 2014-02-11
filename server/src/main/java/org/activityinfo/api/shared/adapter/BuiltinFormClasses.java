package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nullable;

/**
 * In the old api, many "FormClasses" were builtins, defined as part
 * of ActivityInfo's own datamodel. In Api2, these all move to the users
 * control as so become just another FormClass.
 */
public class BuiltinFormClasses {


    /**
     * Partner was a builtin object type in api1. However, we need a different
     * FormClass for each legacy UserDatabase.
     */
    public static FormClass projectFormClass(int databaseId) {

        Cuid classId = CuidAdapter.projectFormClass(databaseId);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(I18N.CONSTANTS.project()));

        // add the project's name
        FormField nameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        formClass.addElement(nameField);

        return formClass;
    }


    static class ActivityAdapter implements Function<SchemaDTO, FormClass> {

        private int activityId;

        public ActivityAdapter(int activityId) {
            this.activityId = activityId;
        }

        @Nullable
        @Override
        public FormClass apply(@Nullable SchemaDTO schemaDTO) {
            ActivityDTO activity = schemaDTO.getActivityById(activityId);
            ActivityUserFormBuilder builder = new ActivityUserFormBuilder(activity);
            return builder.build();
        }
    }
}
