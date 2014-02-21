package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Pair;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.function.BiFunction;

import java.util.Set;


public class SiteInstanceAdapter extends BiFunction<SchemaDTO, SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(final SchemaDTO schema, SiteDTO site) {
        final FormInstance instance = new FormInstance(site.getCuid(), site.getActivityCuid());

        instance.set(CuidAdapter.partnerField(site.getActivityId()),
                Sets.newHashSet(CuidAdapter.cuid(CuidAdapter.PARTNER_DOMAIN, site.getPartnerId())));

        instance.set(CuidAdapter.locationField(site.getActivityId()),
                Sets.newHashSet(CuidAdapter.cuid(CuidAdapter.LOCATION_DOMAIN, site.getLocationId())));

        final ActivityDTO activity = schema.getActivityById(site.getActivityId());

        for (String propertyName : site.getPropertyNames()) {
            if (propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                int indicatorId = IndicatorDTO.indicatorIdForPropertyName(propertyName);
                Double value = site.getIndicatorValue(indicatorId);

                if (value != null) {
                    instance.set(CuidAdapter.indicatorField(indicatorId), value);
                }
            } else if (propertyName.startsWith(AttributeDTO.PROPERTY_PREFIX)) {
                final int attributeId = AttributeDTO.idForPropertyName(propertyName);
                if (site.getAttributeValue(attributeId)) {
                    final Cuid attributeCuid = CuidAdapter.attributeId(attributeId);

                    for (AttributeGroupDTO attributeGroup : activity.getAttributeGroups()) {
                        if (attributeGroup.getAttributeIds().contains(attributeId)) {
                            final Cuid attributeGroupCuid = CuidAdapter.attributeGroupField(activity, attributeGroup);
                            final Object set = instance.get(attributeGroupCuid);
                            if (set instanceof Set) {
                                ((Set) set).add(attributeCuid);
                            } else {
                                instance.set(attributeGroupCuid, Sets.newHashSet(attributeCuid));
                            }
                        }
                    }
                }
            }
        }

        return instance;
    }
}

