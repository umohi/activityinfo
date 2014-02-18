package org.activityinfo.api2.client.promises;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import javax.annotation.Nullable;

/**
 * AsyncFunction that takes no arguments
 */
public abstract class AsyncTask<T> implements AsyncFunction<Void, T> {

    @Override
    public void apply(@Nullable Void input, AsyncCallback<T> callback) {
        apply(callback);
    }

    protected abstract void apply(AsyncCallback<T> callback);

}
