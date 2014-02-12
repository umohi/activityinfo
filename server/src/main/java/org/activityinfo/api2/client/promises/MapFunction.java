package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Transforms an input list using a transform function.
 */
public class MapFunction<F, T> implements Function<Iterable<F>, List<T>> {

    private final Function<F, T> transform;

    public MapFunction(Function<F, T> transform) {
        this.transform = transform;
    }

    @Nullable
    @Override
    public List<T> apply(@Nullable Iterable<F> input) {
        return Lists.newArrayList(Iterables.transform(input, transform));
    }
}
