package org.activityinfo.api.shared.adapter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.Namespace;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.ui.full.client.i18n.I18N;

import java.util.List;

/**
 * Adapts a Legacy "Activity" model to a FormClass
 */
public class ActivityUserFormBuilder {


    private final ActivityDTO activity;
    private List<FormElement> siteElements = Lists.newArrayList();

    private FormClass siteForm;

    public ActivityUserFormBuilder(ActivityDTO activity) {
        this.activity = activity;
    }

    public FormClass build() {
        siteForm = new FormClass(CuidAdapter.cuid(CuidAdapter.ACTIVITY_DOMAIN, activity.getId()));

        FormField partnerField = new FormField(CuidAdapter.partnerField(activity.getId()));
        partnerField.addSuperProperty(Namespace.REPORTED_BY);
        partnerField.setLabel(new LocalizedString(I18N.CONSTANTS.partner()));
        partnerField.setRange(Namespace.PARTNER);
        partnerField.setType(FormFieldType.REFERENCE);
        siteForm.addElement(partnerField);


        FormField locationField = new FormField(CuidAdapter.locationField(activity.getId()));
        locationField.setLabel(new LocalizedString(activity.getLocationType().getName()));
        locationField.setRange(Namespace.locationType(activity.getLocationTypeId()));
        locationField.setType(FormFieldType.REFERENCE);

        for (AttributeGroupDTO group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(CuidAdapter.attributeGroupField(activity, group));
            attributeField.setLabel(new LocalizedString(group.getName()));
            attributeField.setRange(CuidAdapter.attributeGroupClass(group).asIri());
            attributeField.setType(FormFieldType.REFERENCE);
            attributeField.setRequired(group.isMandatory());
            if (group.isMultipleAllowed()) {
                attributeField.setCardinality(FormFieldCardinality.MULTIPLE);
            } else {
                attributeField.setCardinality(FormFieldCardinality.SINGLE);
            }
            siteForm.addElement(attributeField);
        }

        for (IndicatorGroup group : activity.groupIndicators()) {
            if (Strings.isNullOrEmpty(group.getName())) {
                addIndicators(siteForm, group);
            } else {
                FormSection section = new FormSection(CuidAdapter.activityFormSection(activity.getId(), group.getName()));
                section.setLabel(new LocalizedString(group.getName()));

                addIndicators(section, group);

                siteForm.addElement(section);
            }
        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activity.getId()));
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(new LocalizedString(I18N.CONSTANTS.comments()));
        siteForm.addElement(commentsField);

        return siteForm;
    }

    private void addIndicators(FormElementContainer container, IndicatorGroup group) {
        for (IndicatorDTO indicator : group.getIndicators()) {
            FormField field = new FormField(CuidAdapter.indicatorField(indicator.getId()));
            field.setLabel(new LocalizedString(indicator.getName()));
            field.setDescription(new LocalizedString(indicator.getDescription()));
            field.setType(FormFieldType.QUANTITY);
            container.addElement(field);
        }
    }
}
