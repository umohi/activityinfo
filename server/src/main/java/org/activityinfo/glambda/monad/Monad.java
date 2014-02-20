package org.activityinfo.glambda.monad;

import com.google.common.base.Function;

/**
 * Created by alex on 2/20/14.
 */
public interface Monad<T> {

    T unwrap();

    /**
     * The fmap operation, with type (t→u) → M t→M u, takes a function between two types and
     * produces a function that does the "same thing" to values in the monad.
     */
    <F, T> Function<? extends Monad<F>, ? extends Monad<T>> fmap(Function<F, T> function);


    <F> Monad<F> then(Function<F, T> function);


}
