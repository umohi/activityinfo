package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.activityinfo.api.shared.model.AdminEntityDTO;
import org.activityinfo.api.shared.model.LocationDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;


/**
 * Converts a legacy LocationDTO to a FormInstance
 */
public class LocationInstanceAdapter implements Function<LocationDTO, FormInstance> {

    private final LocationClassAdapter classAdapter;

    public LocationInstanceAdapter(Cuid formClassId) {
        Preconditions.checkArgument(formClassId.getDomain() == CuidAdapter.LOCATION_TYPE_DOMAIN);
        classAdapter = new LocationClassAdapter(CuidAdapter.getLegacyIdFromCuid(formClassId));
    }

    @Override
    public FormInstance apply(LocationDTO input) {
        Cuid instanceId = CuidAdapter.locationInstanceId(input.getId());
        FormInstance instance = new FormInstance(instanceId, classAdapter.getFormClassId());
        instance.set(classAdapter.getNameFieldId(), input.getName());
        instance.set(classAdapter.getAxeField(), input.getAxe());
        instance.set(classAdapter.getPointFieldId(), input.getPoint());

        for(AdminEntityDTO entity : input.getAdminEntities()) {
            instance.set(classAdapter.getAdminFieldId(entity.getLevelId()),
                    CuidAdapter.adminEntityInstanceId(entity.getId()));
        }
        return instance;
    }
}
