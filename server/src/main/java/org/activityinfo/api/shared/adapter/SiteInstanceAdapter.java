package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.AttributeDTO;
import org.activityinfo.api.shared.model.IndicatorDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.shared.form.FormInstance;


public class SiteInstanceAdapter implements Function<SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(SiteDTO site) {
        FormInstance instance = new FormInstance(site.getCuid(), site.getActivityCuid());

        instance.set(CuidAdapter.partnerField(site.getActivityId()),
                Lists.newArrayList(CuidAdapter.cuid(CuidAdapter.PARTNER_DOMAIN, site.getPartnerId())));

        instance.set(CuidAdapter.locationField(site.getActivityId()),
                Lists.newArrayList(CuidAdapter.cuid(CuidAdapter.LOCATION_DOMAIN, site.getLocationId())));

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
//                    instance.set(, Sets.newHashSet(CuidAdapter.attributeField(attributeId)));
                }
            }
        }

        return instance;
    }
}

