package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.shared.adapter.bindings.SiteBinding;
import org.activityinfo.api.shared.adapter.bindings.SiteBindingFactory;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.function.BiFunction;


public class SiteInstanceAdapter extends BiFunction<SchemaDTO, SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(final SchemaDTO schema, SiteDTO site) {
        SiteBinding binding = new SiteBindingFactory(site.getActivityId()).apply(schema);
        return binding.newInstance(site);
    }
}

