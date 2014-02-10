package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldCardinality;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.server.database.hibernate.entity.AdminLevel;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.activityinfo.api.shared.adapter.CuidAdapter.adminLevelFormClass;

/**
 * In the old api, many "FormClasses" were builtins, defined as part
 * of ActivityInfo's own datamodel. In Api2, these all move to the users
 * control as so become just another FormClass.
 */
public class BuiltinFormClasses {


    /**
     * Partner was a builtin object type in api1. However, we need a different
     * FormClass for each legacy UserDatabase.
     *
     */
    public static FormClass partnerFormClass(int databaseId) {

        Cuid classId = CuidAdapter.partnerFormClass(databaseId);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(I18N.CONSTANTS.partner()));

        // add the partner's name
        FormField nameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        formClass.addElement(nameField);

        // partner full name
        FormField fullNameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        fullNameField.setLabel(new LocalizedString(I18N.CONSTANTS.fullName()));
        fullNameField.setType(FormFieldType.FREE_TEXT);
        formClass.addElement(fullNameField);

        return formClass;
    }


    /**
     * Partner was a builtin object type in api1. However, we need a different
     * FormClass for each legacy UserDatabase.
     *
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


    public static class AttributeGroupAdapter implements Function<SchemaDTO, FormClass> {

        private int attributeGroupId;

        public AttributeGroupAdapter(int attributeGroupId) {
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

    public static class LocationTypeFormClassAdapter implements Function<SchemaDTO, FormClass> {

        private final int locationTypeId;

        public LocationTypeFormClassAdapter(int locationTypeId) {
            this.locationTypeId = locationTypeId;
        }

        @Nullable
        @Override
        public FormClass apply(@Nullable SchemaDTO schema) {
            CountryDTO country = findCountry(schema, locationTypeId);
            LocationTypeDTO locationType = country.getLocationTypeById(locationTypeId);

            Cuid classId = CuidAdapter.locationFormClass(locationTypeId);
            FormClass formClass = new FormClass(classId);
            formClass.setLabel(new LocalizedString(locationType.getName()));

            FormField nameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
            nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
            nameField.setType(FormFieldType.FREE_TEXT);
            nameField.setRequired(true);
            formClass.addElement(nameField);

            FormField axeField = new FormField(CuidAdapter.field(classId, CuidAdapter.AXE_FIELD));
            axeField.setLabel(new LocalizedString(I18N.CONSTANTS.axe()));
            axeField.setType(FormFieldType.FREE_TEXT);
            formClass.addElement(axeField);

            for(AdminLevelDTO level :  findLeaves(country.getAdminLevels())) {
                FormField adminField = new FormField(CuidAdapter.adminField(classId, level.getId()));
                adminField.setLabel(new LocalizedString(level.getName()));
                adminField.setType(FormFieldType.REFERENCE);
                adminField.setCardinality(FormFieldCardinality.SINGLE);
                adminField.setRange(singleton(adminLevelFormClass(level.getId()).asIri()));
                formClass.addElement(adminField);
            }

            FormField pointField = new FormField(CuidAdapter.field(classId, CuidAdapter.GEOMETRY_FIELD));
            pointField.setLabel(new LocalizedString(I18N.CONSTANTS.geographicCoordinatesFieldLabel()));
            pointField.setType(FormFieldType.GEOGRAPHIC_POINT);
            formClass.addElement(pointField);

            return formClass;
        }

        private Collection<AdminLevelDTO> findLeaves(List<AdminLevelDTO> levels) {
            Map<Integer, AdminLevelDTO> idMap = Maps.newHashMap();
            for(AdminLevelDTO level : levels) {
                idMap.put(level.getId(), level);
            }
            for(AdminLevelDTO level : levels) {
                if(!level.isRoot()) {
                    idMap.remove(level.getParentLevelId());
                }
            }
            return idMap.values();
        }

        private CountryDTO findCountry(SchemaDTO schema, int locationTypeId) {
            for(CountryDTO country : schema.getCountries()) {
                for(LocationTypeDTO locationType : country.getLocationTypes()) {
                    if(locationType.getId() == locationTypeId) {
                        return country;
                    }
                }
            }
            throw new IllegalArgumentException("LocationType with id " + locationTypeId + " not found");

        }

    }

    public static class AdminLevelFormClassAdapter implements Function<SchemaDTO, FormClass> {

        private final int adminLevelId;

        public AdminLevelFormClassAdapter(int adminLevelId) {
            this.adminLevelId = adminLevelId;
        }

        @Nullable
        @Override
        public FormClass apply(@Nullable SchemaDTO schema) {
            AdminLevelDTO adminLevel = schema.getAdminLevelById(adminLevelId);

            Cuid classId = adminLevelFormClass(adminLevelId);
            FormClass formClass = new FormClass(classId);
            formClass.setLabel(new LocalizedString(adminLevel.getName()));

            if(adminLevel.isRoot()) {
                // TODO add country field
            } else {
                AdminLevelDTO parentLevel = schema.getAdminLevelById(adminLevel.getParentLevelId());
                FormField parentField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARENT_FIELD));
                parentField.setLabel(new LocalizedString(parentLevel.getName()));
                parentField.setRange(adminLevelFormClass(adminLevel.getParentLevelId()).asIri());
                parentField.setType(FormFieldType.REFERENCE);
                parentField.setRequired(true);
                formClass.addElement(parentField);
            }

            FormField nameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
            nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
            nameField.setType(FormFieldType.FREE_TEXT);
            nameField.setRequired(true);
            formClass.addElement(nameField);

            FormField codeField = new FormField(CuidAdapter.field(classId, CuidAdapter.CODE_FIELD));
            codeField.setLabel(new LocalizedString(I18N.CONSTANTS.codeFieldLabel()));
            codeField.setType(FormFieldType.FREE_TEXT);
            formClass.addElement(codeField);


            return formClass;
        }
    }
}
