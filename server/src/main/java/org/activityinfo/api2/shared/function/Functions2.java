package org.activityinfo.api2.shared.function;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Additional static functions that operate on {@link com.google.common.base.Function}
 */
public class Functions2 {

    public static <T, R> Function<Void, R> closeOver(final Function<T, R> function, final T argument) {
        return new Function<Void, R>() {
            @Nullable
            @Override
            public R apply(@Nullable Void aVoid) {
                return function.apply(argument);
            }
        };
    }

    public static <T> Function<T, List<T>> toList() {
        return new Function<T, List<T>>() {
            @Override
            public List<T> apply(@Nullable T input) {
                return Collections.singletonList(input);
            }
        };
    }
}
