package org.activityinfo.api2.shared.function;

import com.google.common.base.Function;

import javax.annotation.Nullable;

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
}
