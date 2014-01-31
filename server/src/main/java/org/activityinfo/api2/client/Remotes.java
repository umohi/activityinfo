package org.activityinfo.api2.client;

import com.google.common.base.Function;

/**
 * Functions that generator or operate on {@link Remote}s
 */
public class Remotes {

    public static <X> Remote<X> error(final Throwable exception) {
        return new Remote<X>() {
            @Override
            public Promise<X> fetch() {
                return Promise.rejected(exception);
            }
        };
    }

    public static <F, T> Remote<F> transform(final Remote<T> domain, final Function<T, F> transform) {
        return new Remote<F>() {
            @Override
            public Promise<F> fetch() {
                try {
                    return domain.fetch().then(transform);
                } catch(Throwable caught) {
                    return Promise.rejected(caught);
                }
            }
        };
    }
}
