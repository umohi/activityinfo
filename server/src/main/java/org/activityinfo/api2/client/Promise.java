package org.activityinfo.api2.client;


import com.google.common.collect.Lists;
import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.promises.AsyncFunctions;
import org.activityinfo.api2.client.promises.Retryable;
import org.activityinfo.api2.shared.Pair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The Promise interface represents a proxy for a value not necessarily known at its creation time.
 * It allows you to associate handlers to an asynchronous action's eventual success or failure.
 * This let asynchronous methods to return values like synchronous methods: instead of the final value,
 * the asynchronous method returns a promise of having a value at some point in the future.
 *
 * @param <T> the type of the promised value
 */
public final class Promise<T> implements AsyncCallback<T>, Retryable {


    public enum State {

        /**
         * The action relating to the promise succeeded
         */
        FULFILLED,

        /**
         * The action relating to the promise failed
         */
        REJECTED,

        /**
         * Hasn't fulfilled or rejected yet
         */
        PENDING
    }

    private final Retryable parent;

    private State state = State.PENDING;
    private T value;
    private Throwable exception;

    private List<AsyncCallback<? super T>> callbacks = null;

    public Promise() {
        this.parent = new Retryable() {
            @Override
            public void retry() {
                reject(new UnsupportedOperationException());
            }
        };
    }

    private Promise(Retryable parent) {
        this.parent = parent;
    }

    public Promise(final AsyncFunction<Void, T> operation) {
        this.parent = new Retryable() {
            @Override
            public void retry() {
                operation.apply(null, Promise.this);
            }
        };
        operation.apply(null, this);
    }

    public State getState() {
        return state;
    }

    public boolean isSettled() {
        return state == State.FULFILLED || state == State.REJECTED;
    }

    public final void resolve(T value) {
        if (state != State.PENDING) {
            return;
        }
        this.value = value;
        this.state = State.FULFILLED;

        publishFulfillment();
    }

    public void retry() {
        if(state == State.REJECTED) {
            state = State.PENDING;
            parent.retry();
        }
    }

    public void then(AsyncCallback<? super T> callback) {
        switch (state) {
            case PENDING:
                if (callbacks == null) {
                    callbacks = Lists.newArrayList();
                }
                callbacks.add(callback);
                break;
            case FULFILLED:
                callback.onSuccess(value);
                break;
            case REJECTED:
                callback.onFailure(exception);
                break;
        }
    }

    public <F> Promise<F> then(final Function<? super T, F> f) {
        final Promise<F> chained = new Promise<F>(parent);
        then(new AsyncCallback<T>() {

            @Override
            public void onFailure(Throwable caught) {
                chained.reject(caught);
            }

            @Override
            public void onSuccess(T result) {
                try {
                    chained.resolve(f.apply(result));
                } catch (Throwable caught) {
                    chained.reject(caught);
                }
            }
        });
        return chained;
    }

    public <F> Promise<F> then(final AsyncFunction<T, F> f) {
        final Promise<F> chained = new Promise<F>(this);
        then(new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                chained.reject(caught);
            }

            @Override
            public void onSuccess(T result) {
                f.apply(result, new AsyncCallback<F>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        chained.reject(caught);
                    }

                    @Override
                    public void onSuccess(F result) {
                        chained.resolve(result);
                    }
                });
            }
        });
        return chained;
    }

    @Override
    public void onFailure(Throwable caught) {
        reject(caught);
    }

    @Override
    public void onSuccess(T result) {
        resolve(result);
    }

    public final void reject(Throwable caught) {
        if (state != State.PENDING) {
            return;
        }
        this.exception = caught;
        this.state = State.REJECTED;

        publishRejection();
    }

    private void publishRejection() {
        if (callbacks != null) {
            for (AsyncCallback<? super T> callback : callbacks) {
                callback.onFailure(exception);
            }
        }
    }

    private void publishFulfillment() {
        if (callbacks != null) {
            for (AsyncCallback<? super T> callback : callbacks) {
                callback.onSuccess(value);
            }
        }
    }

    public static <F, T> Promise<T> promise(final F input, final AsyncFunction<F, T> function) {
        return new Promise<T>(AsyncFunctions.apply(input, function));
    }

    public static <T> Promise<T> resolved(T value) {
        return new Promise<T>(AsyncFunctions.constant(value));
    }


    public static <X> Promise<X> rejected(Throwable exception) {
        Promise<X> promise = new Promise<X>();
        promise.reject(exception);
        return promise;
    }

    public static <X,Y> Promise<Pair<X,Y>> pair(final Promise<X> x, final Promise<Y> y) {
        return new Promise<>(new AsyncFunction<Void, Pair<X,Y>>() {
            @Override
            public void apply(Void noInput, final AsyncCallback<Pair<X, Y>> callback) {
                x.retry();
                y.retry();
                x.then(new AsyncCallback<X>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(final X x) {
                        y.then(new AsyncCallback<Y>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            @Override
                            public void onSuccess(Y y) {
                                callback.onSuccess(new Pair<X, Y>(x, y));
                            }
                        });
                    }
                });
            }
        });
    }

    public static <X> Promise<List<X>> all(final List<Promise<X>> promises) {

        if(promises.isEmpty()) {
            return Promise.resolved(Collections.<X>emptyList());
        }

        return new Promise<>(new AsyncFunction<Void, List<X>>() {
            @Override
            public void apply(Void noInput, final AsyncCallback<List<X>> callback) {
                for(Promise<X> promise : promises) {
                    promise.retry();
                }

                final List<X> results = Lists.newArrayList();
                final Counter remaining = new Counter(promises.size());

                for(int i=0;i!=promises.size();++i) {
                    results.add(null);
                    final int promiseIndex = i;
                    promises.get(i).then(new AsyncCallback<X>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(X result) {
                            results.set(promiseIndex, result);
                            remaining.decrement();
                            if (remaining.isZero()) {
                                callback.onSuccess(results);
                            }
                        }
                    });
                }
            }
        });
    }

    private static class Counter {
        private int value;

        public Counter(int value) {
            this.value = value;
        }

        public void decrement() {
            value--;
        }

        public boolean isZero() {
            return value == 0;
        }
    }

    @Override
    public String toString() {
        switch(state) {
            case FULFILLED:
                return "<fulfilled: " + value + ">";
            case REJECTED:
                return "<rejected: " + exception.getClass().getSimpleName() + ">";
            default:
            case PENDING:
                return "<pending>";
        }
    }
}
