package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Unions a set of FormInstance lists together
 */
public class UnionFunction implements Function<List<List<FormInstance>>, List<FormInstance>> {
    @Override
    public List<FormInstance> apply(List<List<FormInstance>> input) {
        List<FormInstance> union = Lists.newArrayList();
        for(List<FormInstance> resultSet : input) {
            union.addAll(resultSet);
        }
        return union;
    }
}
