package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Pair;
import org.activityinfo.api2.shared.function.BiConsumer;
import org.activityinfo.api2.shared.function.BiFunction;
import org.activityinfo.api2.shared.function.Consumer;

import javax.annotation.Nullable;

import static org.activityinfo.api2.client.promises.Reducers.nullReducer;

/**
 * Created by alex on 2/17/14.
 */
public class AsyncFunctions {
//
//    private static Scheduler SCHEDULER = Scheduler.get();
//
//    /**
//     * Sets the Scheduler used for incremental tasks. This method can be used to
//     * provide a stub that runs on the JVM for unit testing.
//     */
//    public static void setScheduler(Scheduler scheduler) {
//        SCHEDULER = scheduler;
//    }
//
//
//
//
//    /**
//     * Given a function {@code (a x b) -> c}, this function will create a new function
//     * {@code a -> (b -> c)}
//     */
//    public static <A, B, C> Function<A, Function<B, C>> curry(final BiFunction<A, B, C> function) {
//        return new Function<A, Function<B, C>>() {
//            @Override
//            public Function<B, C> apply(@Nullable final A a) {
//                return new Function<B, C>() {
//                    @Nullable
//                    @Override
//                    public C apply(@Nullable B b) {
//                        return function.apply(a, b);
//                    }
//                };
//            }
//        };
//    }
//
//    public static <T, U> Function<T, Consumer<U>> curry(final BiConsumer<T, U> consumer) {
//        return new Function<T, Consumer<U>>() {
//            @Override
//            public Consumer<U> apply(@Nullable final T t) {
//                return new Consumer<U>() {
//                    @Override
//                    public void accept(U u) {
//                        consumer.accept(t, u);
//                    }
//                };
//            }
//        };
//    }
//
//    /**
//     * Given a function {@code (a x b) -> c}, this function will create a new function
//     * {@code a -> (b -> c)}
//     */
//    public static <A, B, C> Function<A, Function<B, C>> curry(final Function<Pair<A, B>, C> function) {
//        return new Function<A, Function<B, C>>() {
//            @Override
//            public Function<B, C> apply(@Nullable final A a) {
//                return new Function<B, C>() {
//                    @Nullable
//                    @Override
//                    public C apply(@Nullable B b) {
//                        return function.apply(new Pair<>(a,b));
//                    }
//                };
//            }
//        };
//    }
//
//    /**
//     * Given a function {@code (a x b) -> c}, this function will create a new function
//     * {@code a -> (b -> c)}
//     */
//    public static <A, B, C> Function<A, AsyncFunction<B, C>> curry(final AsyncFunction<Pair<A, B>, C> function) {
//       return new Function<A, AsyncFunction<B, C>>() {
//           @Override
//           public AsyncFunction<B, C> apply(final A a) {
//               return new AsyncFunction<B, C>() {
//                   @Override
//                   public void apply(@Nullable B b, AsyncCallback<C> callback) {
//                       function.apply(new Pair<>(a, b), callback);
//                   }
//               };
//           }
//       };
//    }
//
//    public static <A, B, C> ComposeFunction<A, B, C> compose() {
//        return new ComposeFunction<>();
//    }
//
//
//    public static <T> AsyncFunction<Object, T> failure(final Throwable caught) {
//        return new AsyncFunction<Object, T>() {
//            @Override
//            public void apply(@Nullable Object o, AsyncCallback<T> callback) {
//                callback.onFailure(caught);
//            }
//        };
//    }
//
//    /**
//     * Partially applies a function's first argument, returning a new function
//     * that takes one less argument
//     */
//    public static <A, B, ResultT> AsyncFunction<B, ResultT> partialApply(
//            final AsyncFunction<Pair<A, B>, ResultT> function,
//            final A a) {
//
//        return new AsyncFunction<B, ResultT>() {
//            @Override
//            public void apply(@Nullable B b, AsyncCallback<ResultT> callback) {
//                function.apply(new Pair<>(a, b), callback);
//            }
//        };
//    }
//
//    /**
//     * Partially apply the given function using the first two supplied arguments, returning a function with
//     * an arity reduced by two
//     */
//    public static <A, B, C, ResultT> AsyncFunction<C, ResultT> partialApply(
//            final AsyncFunction<Pair<A, Pair<B, C>>, ResultT> function,
//            final A a,
//            final B b) {
//
//        return new AsyncFunction<C, ResultT>() {
//            @Override
//            public void apply(@Nullable C c, AsyncCallback<ResultT> callback) {
//                function.apply(new Pair<>(a, new Pair<>(b, c)), callback);
//            }
//        };
//    }

}
