package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.activityinfo.api.shared.model.AdminLevelDTO;
import org.activityinfo.api.shared.model.CountryDTO;
import org.activityinfo.api.shared.model.LocationTypeDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.shared.*;
import org.activityinfo.api2.shared.application.ApplicationProperties;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldCardinality;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nullable;
import java.util.Set;

import static org.activityinfo.api.shared.adapter.CuidAdapter.adminLevelFormClass;

/**
* Creates a {@code FormClass} for a LocationType given a legacy SchemaDTO.
*/
public class LocationClassAdapter implements Function<SchemaDTO, FormClass> {

    private final int locationTypeId;
    private Cuid classId;

    public LocationClassAdapter(int locationTypeId) {
        this.locationTypeId = locationTypeId;
        classId = CuidAdapter.locationFormClass(this.locationTypeId);
    }

    public static Cuid getPointFieldId(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.GEOMETRY_FIELD);
    }

    public static Cuid getAxeFieldId(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.AXE_FIELD);
    }

    public static Cuid getNameFieldId(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.NAME_FIELD);
    }

    public static Cuid getAdminFieldId(Cuid classId) {
        return CuidAdapter.adminField(classId, CuidAdapter.ADMIN_PARENT_FIELD);
    }

    @Nullable
    @Override
    public FormClass apply(@Nullable SchemaDTO schema) {
        CountryDTO country = findCountry(schema, locationTypeId);
        LocationTypeDTO locationType = country.getLocationTypeById(locationTypeId);

        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(locationType.getName()));

        FormField nameField = new FormField(getNameFieldId(classId));
        nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        nameField.setSuperProperty(ApplicationProperties.LABEL_PROPERTY);
        formClass.addElement(nameField);

        FormField axeField = new FormField(getAxeFieldId(classId));
        axeField.setLabel(new LocalizedString(I18N.CONSTANTS.axe()));
        axeField.setType(FormFieldType.FREE_TEXT);
        formClass.addElement(axeField);

        // the range for the location object is any AdminLevel in this country
        Set<Cuid> adminRange = Sets.newHashSet();
        for(AdminLevelDTO level : country.getAdminLevels()) {
            adminRange.add(adminLevelFormClass(level.getId()));
        }

        FormField adminField = new FormField(getAdminFieldId(classId));
        adminField.setLabel(new LocalizedString(I18N.CONSTANTS.adminEntities()));
        adminField.setType(FormFieldType.REFERENCE);
        adminField.setCardinality(FormFieldCardinality.SINGLE);
        adminField.setRange(adminRange);
        formClass.addElement(adminField);

        FormField pointField = new FormField(getPointFieldId(classId));
        pointField.setLabel(new LocalizedString(I18N.CONSTANTS.geographicCoordinatesFieldLabel()));
        pointField.setType(FormFieldType.GEOGRAPHIC_POINT);
        formClass.addElement(pointField);

        return formClass;
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
