package org.activityinfo.api2.client.promises;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import java.util.Iterator;

/**
 *
 */
class AsyncMapVoidFunction<T> implements AsyncFunction<Iterable<T>, Void> {
    private AsyncFunction<T, Void> action;

    public AsyncMapVoidFunction(AsyncFunction<T, Void> action) {
        this.action = action;
    }

    @Override
    public void apply(Iterable<T> input, AsyncCallback<Void> callback) {
        matchNext(input.iterator(), callback);
    }

    private void matchNext(final Iterator<T> iterator, final AsyncCallback<Void> callback) {

        if(!iterator.hasNext()) {
            callback.onSuccess(null);
        } else {
            try {
                action.apply(iterator.next(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        matchNext(iterator, callback);
                    }
                });
            } catch(Throwable caught) {
                callback.onFailure(caught);
            }
        }
    }
}
