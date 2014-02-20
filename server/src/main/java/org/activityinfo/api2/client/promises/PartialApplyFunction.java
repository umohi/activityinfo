package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import org.activityinfo.api2.shared.Pair;

import javax.annotation.Nullable;

/**
 *
 * The Partial Application Function, which given a function {@code (a x b) -> c}
 * and the first argument {@code a}, closes over the argument and
 * returns a new function {@code a -> c}
 *
 * @see {@linkplain http://en.wikipedia.org/wiki/Partial_application}
 */
public class PartialApplyFunction<A, B, ResultT>
        implements Function<
            Pair<
                Function<Pair<A,B>, ResultT>,
                A >,
            Function<B, ResultT>> {

    @Override
    public Function<B, ResultT> apply(final Pair<Function<Pair<A, B>, ResultT>, A> arguments) {
        final A a = arguments.getB();
        return new Function<B, ResultT>() {
            @Nullable
            @Override
            public ResultT apply(@Nullable B b) {
                return arguments.getA().apply(new Pair<>(a, b));
            }
        };
    }
}
