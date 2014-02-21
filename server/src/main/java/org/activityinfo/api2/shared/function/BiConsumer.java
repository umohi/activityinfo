package org.activityinfo.api2.shared.function;

/**
 * Convenience base class for a {@code BiFunction} that accepts two input arguments and returns no result.
 *
 * <p>This is the two-arity specialization of Consumer. Unlike most other functional interfaces,
 * BiConsumer is expected to operate via side-effects.
 */
public abstract class BiConsumer<T, U> extends BiFunction<T, U, Void> {

    public abstract void accept(T t, U u);

    @Override
    public final Void apply(T t, U u) {
        accept(t, u);
        return null;
    }
}
