package org.activityinfo.api.shared.adapter;


import org.activityinfo.api.shared.model.IndicatorDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.Namespace;
import org.activityinfo.api2.shared.form.UserFormInstance;

/**
 * Creates a {@code UserFormInstance} from {@code Site}
 *
 * <p>Sites were basically the equivalent of UserFormInstances in the old API,
 * but now most of the objects in the old API are going to become UserForms/Instances
 * so we need to expose it as something more generic.
 */
public class InstanceAdapters {


    /**
     * Creates a {@code UserFormInstance} from a legacy {@code Site}
     * @param site
     * @return
     */
    public static UserFormInstance fromSite(SiteDTO site) {
        UserFormInstance instance = new UserFormInstance(site.getIri(), site.getActivityIri());

        instance.set(Namespace.REPORTED_BY, Cuids.toIri(CuidAdapter.PARTNER_DOMAIN, site.getPartnerId()).asString());

        instance.set(Namespace.LOCATED_AT, Cuids.toIri(CuidAdapter.LOCATION_DOMAIN, site.getLocationId()).asString());

        for(String propertyName : site.getPropertyNames()) {
            if(propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                int indicatorId = IndicatorDTO.indicatorIdForPropertyName(propertyName);
                Double value = site.getIndicatorValue(indicatorId);

                if(value != null) {
                    // todo replace cuid with namespace since for form definition we are using namespace iri
                    // if we need cuid everywhere then we should change form definition adaptor
                    // final Iri iri = CuidAdapter.iri(CuidAdapter.INDICATOR_DOMAIN, indicatorId);
                    final Iri iri = Namespace.indicatorProperty(indicatorId);
                    instance.set(iri, value);
                }
            }
        }

        return instance;
    }
}
