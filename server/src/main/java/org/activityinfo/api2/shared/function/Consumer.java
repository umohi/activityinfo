package org.activityinfo.api2.shared.function;

import com.google.common.base.Function;

/**
 * Represents an operation that accepts a single input argument and returns no result.
 * Unlike most other functional interfaces, Consumer is expected to operate via side-effects.
 */
public abstract class Consumer<T> implements Function<T, Void> {


    public final Void apply(T t) {
        accept(t);
        return null;
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param t input
     */
    public abstract void accept(T t);
}
