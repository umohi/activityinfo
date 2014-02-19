package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by alex on 2/17/14.
 */
public class AsyncFunctions {


    /**
     * Creates a new asynchronous map function, which will take as its input an {@code Iterable} containing
     * items of class {@code T}, and apply the given {@code function} asynchronously to each item in sequence
     * before returning a List of the results asynchronously.
     *
     * @param function
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> AsyncFunction<Iterable<F>,List<T>> map(AsyncFunction<F, T> function) {
        return new AsyncMapFunction<>(function);
    }

    public static <F> AsyncFunction<Iterable<F>, Void> mapAction(AsyncFunction<F, Void> action) {
        return new AsyncMapVoidFunction<>(action);
    }

    public static <F, T> Function<Iterable<F>, List<T>> map(Function<F, T> function) {
        return new MapFunction<>(function);
    }


    public static AsyncFunction<Void, Void> sequence(List<AsyncFunction<Void, Void>> tasks) {
        return new SequenceFunction(tasks);
    }

    /**
     * Map over an {@code Iterable} of input items of class {@code T} and apply the {@code Action} on
     * each item, spread out over multiple event loop iterations to avoid blocking the main UI thread.
     *
     * @param scheduler
     * @param action
     * @param <T>
     * @return
     */
    public static <T> AsyncFunction<Iterable<T>, Void> mapNonBlocking(Scheduler scheduler, Action<T> action) {
        return new NonBlockingSynchronousMapFunction<>(scheduler, action);
    }

    public static <T> AsyncFunction<T, T> identity() {
        return new AsyncFunction<T, T>() {
            @Override
            public void apply(@Nullable T t, AsyncCallback<T> callback) {
                callback.onSuccess(t);
            }
        };
    }

    public static <T> AsyncFunction<Object, T> constant(final T value) {
        return new AsyncFunction<Object, T>() {
            @Override
            public void apply(@Nullable Object o, AsyncCallback<T> callback) {
                callback.onSuccess(value);
            }
        };
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
     * Captures the provided argument and returns a new nullary async function
     */
    public static <T, F> AsyncFunction<Void, F> apply(final T argument, final AsyncFunction<T, F> function) {
        return new AsyncFunction<Void, F>() {
            @Override
            public void apply(@Nullable Void aVoid, AsyncCallback<F> callback) {
                function.apply(argument, callback);
            }
        };
    }

}
