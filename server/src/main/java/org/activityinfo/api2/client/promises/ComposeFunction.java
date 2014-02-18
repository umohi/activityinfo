package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import org.activityinfo.api2.shared.Pair;

import javax.annotation.Nullable;

/**
 * Composes two function {@code f} and {@code g} as {@code f(g(x))}
 */
public class ComposeFunction<A, B, C> implements Function<
        Pair<
            Function<B, C>,
            Function<A, B> >,
        Function<A, C> >  {

    @Override
    public Function<A, C> apply(final Pair<Function<B, C>, Function<A, B>> functions) {
        return new Function<A, C>() {
            @Nullable
            @Override
            public C apply(@Nullable A x) {
                Function<B, C> f = functions.getA();
                Function<A, B> g = functions.getB();

                return f.apply(g.apply(x));
            }
        };
    }
}
