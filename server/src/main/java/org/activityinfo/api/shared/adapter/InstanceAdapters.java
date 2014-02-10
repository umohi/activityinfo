package org.activityinfo.api.shared.adapter;


import com.google.common.collect.Lists;
import org.activityinfo.api.shared.model.AttributeDTO;
import org.activityinfo.api.shared.model.IndicatorDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * Creates a {@code FormInstance} from {@code Site}
 *
 * <p>Sites were basically the equivalent of UserFormInstances in the old API,
 * but now most of the objects in the old API are going to become UserForms/Instances
 * so we need to expose it as something more generic.
 */
public class InstanceAdapters {


    /**
     * Creates a {@code FormInstance} from a legacy {@code Site}
     * @param site
     * @return
     */
    public static FormInstance fromSite(SiteDTO site) {
        FormInstance instance = new FormInstance(site.getIri(), site.getActivityIri());

        instance.set(CuidAdapter.partnerField(site.getActivityId()),
                Lists.newArrayList(CuidAdapter.cuid(CuidAdapter.PARTNER_DOMAIN, site.getPartnerId())));

        instance.set(CuidAdapter.locationField(site.getActivityId()),
                Lists.newArrayList(CuidAdapter.cuid(CuidAdapter.LOCATION_DOMAIN, site.getLocationId())));

        for(String propertyName : site.getPropertyNames()) {
            if(propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                int indicatorId = IndicatorDTO.indicatorIdForPropertyName(propertyName);
                Double value = site.getIndicatorValue(indicatorId);

                if(value != null) {
                    instance.set(CuidAdapter.indicatorField(indicatorId), value);
                }
            }
        }

        return instance;
    }

    public static FormInstance fromAttribute(AttributeDTO attribute, Cuid formClassId) {
        final FormInstance instance = new FormInstance(CuidAdapter.attributeField(attribute.getId()), formClassId);
        instance.setLabel(new LocalizedString(attribute.getName()));
        return instance;
    }
}
