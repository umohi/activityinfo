package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.client.NotFoundException;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.server.database.hibernate.entity.AttributeGroup;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extracts a list of a {@code AttributeDTO}s from a SchemaDTO and converts them to
 * {@code FormInstances}
*/
public class AttributeInstanceListAdapter implements Function<SchemaDTO, List<FormInstance>> {

    private final Predicate<Cuid> criteria;

    public AttributeInstanceListAdapter(Criteria criteria) {
        this.criteria = CriteriaEvaluation.evaluatePartiallyOnClassId(criteria);
    }

    @Nullable
    @Override
    public List<FormInstance> apply(@Nullable SchemaDTO schema) {

        List<FormInstance> instances = Lists.newArrayList();
        for(UserDatabaseDTO db : schema.getDatabases()) {
            for(ActivityDTO activity : db.getActivities()) {
                for(AttributeGroupDTO group : activity.getAttributeGroups()) {
                    Cuid classId = CuidAdapter.attributeGroupFormClass(group.getId());
                    if(criteria.apply(classId)) {
                        for(AttributeDTO attribute : group.getAttributes()) {
                            Cuid instanceId = CuidAdapter.attributeId(attribute.getId());
                            FormInstance instance = new FormInstance(classId, instanceId);
                            instance.set(CuidAdapter.getFormInstanceLabelCuid(instance), attribute.getName());
                            instances.add(instance);
                        }
                    }
                }
            }
        }

        return instances;
    }
}
