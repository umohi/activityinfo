package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Pair;

import javax.annotation.Nullable;

import static org.activityinfo.api2.client.promises.Reducers.nullReducer;

/**
 * Created by alex on 2/17/14.
 */
public class AsyncFunctions {

    private static Scheduler SCHEDULER = Scheduler.get();

    /**
     * Sets the Scheduler used for incremental tasks. This method can be used to
     * provide a stub that runs on the JVM for unit testing.
     */
    public static void setScheduler(Scheduler scheduler) {
        SCHEDULER = scheduler;
    }

    public static <T, F, G> AsyncMapReduceFunction<T, F, G> mapReduce() {
        return new AsyncMapReduceFunction<>();
    }


    public static <InputT> AsyncFunction<Pair<Function<InputT, Void>, Iterable<InputT>>, Void> incrementalMap() {
        IncrementalMapReduceFunction<InputT, Void, Void> mapReduceFunction =
                new IncrementalMapReduceFunction<>(SCHEDULER);

        return partialApply(mapReduceFunction, nullReducer());
    }

    /**
     * @return an {@code AsyncFunction} which calls the {@code mapFunction} for every element in the input,
     * and then applies the reducer function to the sequence of results of the map function.
     *
     * @param <InputT> The list element type
     * @param <MapT> the result type of the map function
     * @param <ReduceT> the result type of the reducer
     */
    public static <InputT, MapT, ReduceT> AsyncFunction<Iterable<InputT>, ReduceT> mapReduce(
            AsyncFunction<InputT, MapT> mapFunction,
            ReduceFunction<? super MapT, ReduceT> reduceFunction) {

        return partialApply(new AsyncMapReduceFunction<InputT, MapT, ReduceT>(), reduceFunction, mapFunction);
    }

    /**
     *
     * @return an {@code AsyncFunction} which calls the {@code mapFunction} for every element in the input
     * and discards its output
     */
    public static <InputT> AsyncFunction<Iterable<InputT>, Void> map(AsyncFunction<InputT, Void> mapFunction) {

        return partialApply(new AsyncMapReduceFunction<InputT, Void, Void>(), nullReducer(), mapFunction);
    }


    /**
     * Map over an {@code Iterable} of input items of class {@code T} and apply the {@code mapFunction} on
     * each item, spread out over multiple event loop iterations to avoid blocking the main UI thread.
     *
     */
    public static <InputT, MapT, ReduceT> AsyncFunction<Iterable<InputT>, ReduceT> mapReduce(
            Scheduler scheduler,
            Function<InputT, MapT> mapFunction,
            ReduceFunction<? super MapT, ReduceT> reduceFunction) {

        return partialApply(new IncrementalMapReduceFunction<InputT, MapT, ReduceT>(scheduler),
                reduceFunction,
                mapFunction);
    }



    /**
     * Map over an {@code Iterable} of input items of class {@code T} and apply the {@code Action} on
     * each item, spread out over multiple event loop iterations to avoid blocking the main UI thread.
     *
     */
    public static <InputT, MapT> AsyncFunction<Iterable<InputT>, Void> map(Scheduler scheduler,
                                                              Function<InputT, MapT> mapFunction) {

        return partialApply(new IncrementalMapReduceFunction<InputT, MapT, Void>(scheduler),
                nullReducer(),
                mapFunction);
    }



    /**
     * Given a function {@code (a x b) -> c}, this function will create a new function
     * {@code a -> (b -> c)}
     */
    public static <A, B, C> Function<A, Function<B, C>> curry(final Function<Pair<A, B>, C> function) {
        return new Function<A, Function<B, C>>() {
            @Override
            public Function<B, C> apply(@Nullable final A a) {
                return new Function<B, C>() {
                    @Nullable
                    @Override
                    public C apply(@Nullable B b) {
                        return function.apply(new Pair<>(a,b));
                    }
                };
            }
        };
    }

    /**
     * Given a function {@code (a x b) -> c}, this function will create a new function
     * {@code a -> (b -> c)}
     */
    public static <A, B, C> Function<A, AsyncFunction<B, C>> curry(final AsyncFunction<Pair<A, B>, C> function) {
       return new Function<A, AsyncFunction<B, C>>() {
           @Override
           public AsyncFunction<B, C> apply(final A a) {
               return new AsyncFunction<B, C>() {
                   @Override
                   public void apply(@Nullable B b, AsyncCallback<C> callback) {
                       function.apply(new Pair<>(a, b), callback);
                   }
               };
           }
       };
    }

    public static <A, B, C> ComposeFunction<A, B, C> compose() {
        return new ComposeFunction<>();
    }


    public static <T> AsyncFunction<Object, T> failure(final Throwable caught) {
        return new AsyncFunction<Object, T>() {
            @Override
            public void apply(@Nullable Object o, AsyncCallback<T> callback) {
                callback.onFailure(caught);
            }
        };
    }

    /**
     * Partially applies a function's first argument, returning a new function
     * that takes one less argument
     */
    public static <A, B, ResultT> AsyncFunction<B, ResultT> partialApply(
            final AsyncFunction<Pair<A, B>, ResultT> function,
            final A a) {

        return new AsyncFunction<B, ResultT>() {
            @Override
            public void apply(@Nullable B b, AsyncCallback<ResultT> callback) {
                function.apply(new Pair<>(a, b), callback);
            }
        };
    }

    /**
     * Partially apply the given function using the first two supplied arguments, returning a function with
     * an arity reduced by two
     */
    public static <A, B, C, ResultT> AsyncFunction<C, ResultT> partialApply(
            final AsyncFunction<Pair<A, Pair<B, C>>, ResultT> function,
            final A a,
            final B b) {

        return new AsyncFunction<C, ResultT>() {
            @Override
            public void apply(@Nullable C c, AsyncCallback<ResultT> callback) {
                function.apply(new Pair<>(a, new Pair<>(b, c)), callback);
            }
        };
    }

    /**
     * Composes two functions {@code f} and {@code g} into a new asynchronous function equivalent
     * to {@code f(g(x))}
     */
    public static <T, F, G> AsyncFunction<T, F> compose(final AsyncFunction<G, F> f, final Function<T, G> g) {
        return new AsyncFunction<T, F>() {
            @Override
            public void apply(@Nullable T input, AsyncCallback<F> callback) {
                try {
                    f.apply(g.apply(input), callback);
                } catch(Exception e) {
                    callback.onFailure(e);
                }
            }
        };
    }
}
