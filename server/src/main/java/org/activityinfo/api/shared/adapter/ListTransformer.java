package org.activityinfo.api.shared.adapter;


import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public class ListTransformer<F,T> implements Function<List<F>, List<T>> {

    private final Function<F, T> transformer;

    public ListTransformer(Function<F, T> transformer) {
        this.transformer = transformer;
    }

    @Override
    public List<T> apply(List<F> input) {
        List<T> output = Lists.newArrayList();
        for(F inputElement : input) {
            output.add(transformer.apply(inputElement));
        }
        return output;
    }
}
