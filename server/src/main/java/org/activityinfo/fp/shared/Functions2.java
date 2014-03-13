package org.activityinfo.fp.shared;

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

    /**
     * Creates a function which creates a singleton list of its argument.
     *
     * <p>Haskell people would say that this is "unit" function of the List Monad, but
     * somehow "singleton list" is a bit clearer.</p>
     */
    public static <T> Function<T, List<T>> singletonList() {
        return new Function<T, List<T>>() {
            @Override
            public List<T> apply(@Nullable T input) {
                return Collections.singletonList(input);
            }
        };
    }
}
