package org.activityinfo.api.shared.adapter;

import com.google.common.collect.Sets;
import org.activityinfo.api.shared.adapter.bindings.SiteBinding;
import org.activityinfo.api.shared.adapter.bindings.SiteBindingFactory;
import org.activityinfo.api.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.function.BiFunction;

import java.util.Set;


public class SiteInstanceAdapter extends BiFunction<SchemaDTO, SiteDTO, FormInstance> {

    @Override
    public FormInstance apply(final SchemaDTO schema, SiteDTO site) {

        SiteBinding binding = new SiteBindingFactory(site.getActivityId()).apply(schema);
        final FormInstance formInstance = binding.newInstance(site);
        return formInstance;
    }
}

