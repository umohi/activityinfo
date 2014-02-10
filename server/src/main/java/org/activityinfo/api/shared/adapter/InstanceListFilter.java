package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.List;

/**
* Created by alex on 2/10/14.
*/
class InstanceListFilter implements Function<List<FormInstance>, List<FormInstance>> {

    private final Criteria criteria;

    InstanceListFilter(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public List<FormInstance> apply(List<FormInstance> input) {
        List<FormInstance> filtered = Lists.newArrayList();
        for(FormInstance instance : input) {
            if(criteria.apply(instance)) {
                filtered.add(instance);
            }
        }
        return filtered;
    }
}
