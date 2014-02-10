package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.api.shared.model.PartnerDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

public class PartnerInstanceAdapter implements Function<PartnerDTO, FormInstance> {

    private final Cuid classId;

    public PartnerInstanceAdapter(Cuid formClassId) {
        this.classId = formClassId;
    }

    @Override
    public FormInstance apply(PartnerDTO input) {
        FormInstance instance = new FormInstance(
                CuidAdapter.partnerInstanceId(input.getId()),
                classId);

        instance.set(PartnerClassAdapter.getNameField(classId), input.getName());
        instance.set(PartnerClassAdapter.getFullNameField(classId), input.getFullName());
        return instance;
    }
}
