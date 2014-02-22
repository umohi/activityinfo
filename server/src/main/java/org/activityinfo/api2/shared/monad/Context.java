package org.activityinfo.api2.shared.monad;

import com.google.common.base.Function;

/**
 *
 */
public class Context<C, T> implements MonadicValue<T> {
    private C context;
    private T value;

    public Context(C context, T value) {
        this.context = context;
        this.value = value;
    }

    @Override
    public <R> Context<T, R> then(Function<? super T, R> function) {
        return new Context<T, R>(value, function.apply(value));
    }
}
