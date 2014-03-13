package org.activityinfo.legacy.shared.adapter;

import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.shared.BiFunction;
import org.activityinfo.legacy.shared.adapter.bindings.SiteBinding;
import org.activityinfo.legacy.shared.adapter.bindings.SiteBindingFactory;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;


public class SiteInstanceAdapter extends BiFunction<SchemaDTO, SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(final SchemaDTO schema, SiteDTO site) {
        SiteBinding binding = new SiteBindingFactory(site.getActivityId()).apply(schema);
        return binding.newInstance(site);
    }
}

