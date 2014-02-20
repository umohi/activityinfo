package org.activityinfo.glambda.monad;

import com.google.common.base.Function;

/**
 * Created by alex on 2/20/14.
 */
public interface MonadicClass {


    /**
     * Takes a value from a plain type and puts it into a monadic container using the constructor,
     * creating a monadic value. In functional programming jargon, this is the {@code unit} or
     * {@code return} function.
     *
     */
    <ValueT> Monad<ValueT> wrap(ValueT value);


    /**
     * The fmap operation, with type (t→u) → M t→M u, takes a function between two types and
     * produces a function that does the "same thing" to values in the monad.
     */
    <F, T> Function<? extends Monad<F>, ? extends Monad<T>> fmap(Function<F, T> function);



}
