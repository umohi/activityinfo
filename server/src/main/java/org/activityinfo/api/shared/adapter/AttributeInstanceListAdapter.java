package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.AttributeDTO;
import org.activityinfo.api.shared.model.AttributeGroupDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.client.NotFoundException;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extracts a list of a {@code AttributeDTO}s from a SchemaDTO and converts them to
 * {@code FormInstances}
*/
public class AttributeInstanceListAdapter implements Function<SchemaDTO, List<FormInstance>> {

    private final int attributeGroupId;

    public AttributeInstanceListAdapter(int attributeGroupId) {
        this.attributeGroupId = attributeGroupId;
    }

    public AttributeInstanceListAdapter(Cuid formClassId) {
        this.attributeGroupId = CuidAdapter.getLegacyIdFromCuid(formClassId);
    }

    @Nullable
    @Override
    public List<FormInstance> apply(@Nullable SchemaDTO schema) {
        Cuid classId = CuidAdapter.attributeGroupFormClass(attributeGroupId);
        AttributeGroupDTO group = schema.getAttributeGroupById(attributeGroupId);
        if(group == null) {
            throw new NotFoundException(classId.asIri());
        }
        List<AttributeDTO> attributes = group.getAttributes();
        List<FormInstance> instances = Lists.newArrayList();
        for(AttributeDTO attribute : attributes) {
            FormInstance instance = new FormInstance(CuidAdapter.attributeId(attribute.getId()), classId);
            instance.set(CuidAdapter.getFormInstanceLabelCuid(instance), attribute.getName());
            instances.add(instance);
        }
        return instances;
    }
}
