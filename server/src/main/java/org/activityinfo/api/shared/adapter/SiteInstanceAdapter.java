package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.shared.adapter.bindings.SiteBinding;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.function.BiFunction;


public class SiteInstanceAdapter extends BiFunction<SchemaDTO, SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(final SchemaDTO schema, SiteDTO site) {

        ActivityDTO activity = schema.getActivityById(site.getActivityId());
        SiteBinding binding = new SiteBinding(activity);

        return binding.newInstance(site);
    }

}

