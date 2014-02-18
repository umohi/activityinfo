package org.activityinfo.api2.shared.function;

import com.google.common.base.Function;

import java.util.List;

/**
 * Static utility methods pertaining to {@code Function} instances.
 *
 */
public class BiFunctions {

    public static <T, R> Function<Iterable<T>, List<R>> concatMap(Function<T, R> function) {
        return new ConcatMapFunction<T, R>().apply(function);
    }

    public static <T> T foldLeft(T initialValue, BiFunction<T, T, T> operator, Iterable<T> items) {
        return new FoldLeftFunction<T>(initialValue).apply(operator, items);
    }

    public static <T> Function<Iterable<T>, T> foldLeft(T initialValue, BiFunction<T, T, T> operator) {
        return new FoldLeftFunction<T>(initialValue).apply(operator);
    }

}
