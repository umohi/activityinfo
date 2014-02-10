package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.activityinfo.api.shared.model.AdminLevelDTO;
import org.activityinfo.api.shared.model.CountryDTO;
import org.activityinfo.api.shared.model.LocationTypeDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldCardinality;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;
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

    public Cuid getPointFieldId() {
        return CuidAdapter.field(classId, CuidAdapter.GEOMETRY_FIELD);
    }

    public Cuid getAxeField() {
        return CuidAdapter.field(classId, CuidAdapter.AXE_FIELD);
    }

    public Cuid getNameFieldId() {
        return CuidAdapter.field(classId, CuidAdapter.NAME_FIELD);
    }

    public Cuid getAdminFieldId(int adminLevelId) {
        return CuidAdapter.adminField(classId, adminLevelId);
    }

    public Cuid getFormClassId() {
        return classId;
    }

    @Nullable
    @Override
    public FormClass apply(@Nullable SchemaDTO schema) {
        CountryDTO country = findCountry(schema, locationTypeId);
        LocationTypeDTO locationType = country.getLocationTypeById(locationTypeId);

        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(locationType.getName()));

        FormField nameField = new FormField(getNameFieldId());
        nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        formClass.addElement(nameField);

        FormField axeField = new FormField(getAxeField());
        axeField.setLabel(new LocalizedString(I18N.CONSTANTS.axe()));
        axeField.setType(FormFieldType.FREE_TEXT);
        formClass.addElement(axeField);

        for(AdminLevelDTO level : findLeaves(country.getAdminLevels())) {
            FormField adminField = new FormField(getAdminFieldId(level.getId()));
            adminField.setLabel(new LocalizedString(level.getName()));
            adminField.setType(FormFieldType.REFERENCE);
            adminField.setCardinality(FormFieldCardinality.SINGLE);
            adminField.setRange(singleton(adminLevelFormClass(level.getId()).asIri()));
            formClass.addElement(adminField);
        }

        FormField pointField = new FormField(getPointFieldId());
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
