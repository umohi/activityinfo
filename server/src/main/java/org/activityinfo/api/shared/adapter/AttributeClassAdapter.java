package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.api.shared.model.AttributeGroupDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nullable;

/**
 * Converts an {@code AttributeGroupDTO} to a {@code FormClass}
 */
public class AttributeClassAdapter implements Function<SchemaDTO, FormClass> {

    private int attributeGroupId;

    public AttributeClassAdapter(int attributeGroupId) {
        this.attributeGroupId = attributeGroupId;
    }

    @Nullable
    @Override
    public FormClass apply(@Nullable SchemaDTO schema) {
        AttributeGroupDTO group = schema.getAttributeGroupById(attributeGroupId);
        Cuid classId = CuidAdapter.attributeGroupFormClass(group);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(group.getName()));

        // attributes have only one field- the label
        FormField labelField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        labelField.setLabel(new LocalizedString(I18N.CONSTANTS.labelFieldLabel()));
        labelField.setType(FormFieldType.FREE_TEXT);
        labelField.setRequired(true);
        formClass.addElement(labelField);

        return formClass;
    }
}
