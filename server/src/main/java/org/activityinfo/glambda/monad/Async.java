package org.activityinfo.glambda.monad;

import com.google.common.base.Function;

/**
 * Created by alex on 2/20/14.
 */
public class Async<T> implements Monad<T> {


    public Async(T value) {

    }

    @Override
    public T unwrap() {
        return null;
    }

    @Override
    public <X, Y> Function<? extends Async<X>, ? extends Monad<Y>> fmap(Function<X, Y> function) {

    }

    @Override
    public <F> Monad<F> then(Function<F, T> function) {
        return null;
    }

    @Override
    public <F> Monad<F> then() {

    }
}
