package org.activityinfo.api2.shared.monad;

import com.google.common.base.Function;
import org.activityinfo.api2.shared.function.ConcatMapFunction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class ListMonad {

    public static <T> Function<T, List<T>> unit() {
        return new Function<T, List<T>>() {
            @Override
            public List<T> apply(@Nullable T input) {
                return Collections.singletonList(input);
            }
        };
    }

    public static <T, R> Function<? extends Iterable<T>, List<R>> bind(Function<T, R> function) {
        return new ConcatMapFunction<T, R>().apply(function);
    }

}
