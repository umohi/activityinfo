package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.Promise;

import javax.annotation.Nullable;

/**
 * Created by alex on 2/21/14.
 */
public abstract class AsyncConsumer<T> implements Function<T, Promise<Void>> {
    @Override
    public Promise<Void> apply(@Nullable T input) {
        Promise<Void> promise = new Promise<>();
        accept(input, promise);
        return promise;
    }

    public abstract void accept(T input, AsyncCallback<Void> callback);
}
