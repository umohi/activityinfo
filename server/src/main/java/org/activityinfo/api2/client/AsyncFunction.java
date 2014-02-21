package org.activityinfo.api2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.common.base.Function;

/**
 * Created by alex on 2/21/14.
 */
public abstract class AsyncFunction<T, R> implements Function<T, Promise<R>> {

    @Override
    public Promise<R> apply(T t) {
        Promise<R> promise = new Promise<>();
        apply(t, promise);
        return promise;
    }

    public abstract void apply(T t, AsyncCallback<R> callback);
}
