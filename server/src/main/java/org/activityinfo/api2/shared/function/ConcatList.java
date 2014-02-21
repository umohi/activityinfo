package org.activityinfo.api2.shared.function;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Binary operator which concatenates two
 */
public class ConcatList<T> extends BiFunction<List<T>, List<T>, List<T>>
{
    @Override
    public List<T> apply(List<T> x, List<T> y) {
        if(x == null) {
            return y;
        } else {
            List<T> result = Lists.newArrayList();
            result.addAll(x);
            result.addAll(y);
            return result;
        }
    }
}
