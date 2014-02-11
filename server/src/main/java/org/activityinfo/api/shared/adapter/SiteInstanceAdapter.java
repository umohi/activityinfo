package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;


public class SiteInstanceAdapter implements Function<SiteDTO, FormInstance> {

    private final SchemaDTO schemaDTO;

    public SiteInstanceAdapter(SchemaDTO schemaDTO) {
        this.schemaDTO = schemaDTO;
    }

    @Override
    public FormInstance apply(final SiteDTO site) {
        final FormInstance instance = new FormInstance(site.getCuid(), site.getActivityCuid());

        instance.set(CuidAdapter.partnerField(site.getActivityId()),
                Sets.newHashSet(CuidAdapter.cuid(CuidAdapter.PARTNER_DOMAIN, site.getPartnerId())));

        instance.set(CuidAdapter.locationField(site.getActivityId()),
                Sets.newHashSet(CuidAdapter.cuid(CuidAdapter.LOCATION_DOMAIN, site.getLocationId())));

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

                    final ActivityDTO activity = this.schemaDTO.getActivityById(site.getActivityId());
                    for (AttributeGroupDTO attributeGroup : activity.getAttributeGroups()) {
                        if (attributeGroup.getAttributeIds().contains(attributeId)) {
                            instance.set(CuidAdapter.attributeGroupField(activity, attributeGroup), Sets.newHashSet(attributeCuid));
                        }
                    }
                }
            }
        }

        return instance;
    }
}

