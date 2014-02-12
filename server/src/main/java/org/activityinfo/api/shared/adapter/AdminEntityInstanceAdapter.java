package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.api.shared.model.AdminEntityDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

/**
* Converts an AdminEntityDTO to a FormInstance
*/
public class AdminEntityInstanceAdapter implements Function<AdminEntityDTO, FormInstance> {


    @Override
    public FormInstance apply(AdminEntityDTO input) {

        Cuid classId = CuidAdapter.adminLevelFormClass(input.getLevelId());
        Cuid instanceId = CuidAdapter.adminEntityInstanceId(input.getId());
        FormInstance instance = new FormInstance(instanceId, classId);

        // Parent field value
        if(input.getParentId() != null) {
            instance.set(CuidAdapter.field(classId, CuidAdapter.ADMIN_PARENT_FIELD),
                    CuidAdapter.adminEntityInstanceId(input.getParentId()));
        }

        instance.set(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD), input.getName());

        return instance;
    }
}
