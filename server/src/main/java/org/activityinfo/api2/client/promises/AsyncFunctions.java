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


    public static AsyncTask<Void> sequence(List<AsyncTask<Void>> tasks) {
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

    public static <T> AsyncTask<T> constant(final T value) {
        return new AsyncTask<T>() {
            @Override
            protected void apply(AsyncCallback<T> callback) {
                callback.onSuccess(value);
            }
        };
    }

    public static <T> AsyncTask<T> failure(final Throwable caught) {
        return new AsyncTask<T>() {
            @Override
            protected void apply(AsyncCallback<T> callback) {
                callback.onFailure(caught);
            }
        };
    }


    /**
     * "Applies" a function to a given argument and returns a new nullary function that
     * @param argument
     * @param function
     * @param <T>
     * @param <F>
     * @return
     */
    public static <T, F> AsyncTask<F> apply(final T argument, final AsyncFunction<T, F> function) {
        return new AsyncTask<F>() {
            @Override
            protected void apply(AsyncCallback<F> callback) {
                function.apply(argument, callback);
            }
        };
    }

}
