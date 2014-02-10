package org.activityinfo.api2.client;


import com.google.common.collect.Lists;
import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;


/**
 * The Promise interface represents a proxy for a value not necessarily known at its creation time.
 * It allows you to associate handlers to an asynchronous action's eventual success or failure.
 * This let asynchronous methods to return values like synchronous methods: instead of the final value,
 * the asynchronous method returns a promise of having a value at some point in the future.
 *
 * @param <T> the type of the promised value
 */
public final class Promise<T> {



    public interface AsyncOperation<T> {
        void start(Promise<T> promise);
    }

    public static final AsyncOperation NO_OP = new AsyncOperation() {
        @Override
        public void start(Promise promise) {
        }
    };

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

    private final AsyncOperation<T> asyncOperation;

    private State state = State.PENDING;
    private T value;
    private Throwable exception;

    private List<AsyncCallback<T>> callbacks = null;

    public Promise(AsyncOperation<T> asyncOperation) {
        this.asyncOperation = asyncOperation;
        this.asyncOperation.start(this);
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
            asyncOperation.start(this);
        }
    }

    public void then(AsyncCallback<T> callback) {
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

    public <F> Promise<F> then(final Function<T, F> f) {
        final Promise<F> chained = new Promise<F>(this.<F>chainTask());
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
        final Promise<F> chained = new Promise<F>(this.<F>chainTask());
        then(new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                chained.reject(caught);
            }

            @Override
            public void onSuccess(T result) {
                f.apply(result).then(new AsyncCallback<F>() {
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

    private <X> AsyncOperation<X> chainTask() {
        return new AsyncOperation<X>() {
            @Override
            public void start(Promise<X> promise) {
                asyncOperation.start(Promise.this);
            }
        };
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
            for (AsyncCallback<T> callback : callbacks) {
                callback.onFailure(exception);
            }
        }
    }

    private void publishFulfillment() {
        if (callbacks != null) {
            for (AsyncCallback<T> callback : callbacks) {
                callback.onSuccess(value);
            }
        }
    }

    public static <T> Promise<T> resolved(T value) {
        Promise<T> promise = new Promise<T>(NO_OP);
        promise.resolve(value);
        return promise;
    }


    public static <X> Promise<X> rejected(Throwable exception) {
        Promise<X> promise = new Promise<X>(NO_OP);
        promise.reject(exception);
        return promise;
    }

//
//    public static <X> Promise<List<X>> all(final List<Promise<X>> promises) {
//        final Promise<List<X>> all = new Promise<>(new AsyncOperation<List<X>>() {
//            @Override
//            public void start(Promise<List<X>> all) {
//                for(Promise<X> promise : promises) {
//                    promise.retry();
//                }
//            }
//        });
//        for(Promise<X> promise : promises) {
//            promise.then(new AsyncCallback<X>() {
//                @Override
//                public void onFailure(Throwable caught) {
//                    all.reject(caught);
//                }
//
//                @Override
//                public void onSuccess(X result) {
//                    all
//                }
//            });
//        }
//    }

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
