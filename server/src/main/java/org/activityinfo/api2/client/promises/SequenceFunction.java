package org.activityinfo.api2.client.promises;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import java.util.Iterator;

/**
 * Executes a list of nullary async functions in a sequence.
 */
public class SequenceFunction implements AsyncFunction<Void, Void> {

    private final Iterable<? extends AsyncFunction<Void, Void>> tasks;

    public SequenceFunction(Iterable<? extends AsyncFunction<Void, Void>> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void apply(Void noInput, AsyncCallback<Void> callback) {
        executeNext(tasks.iterator(), callback);
    }

    private void executeNext(final Iterator<? extends AsyncFunction<Void, Void>> iterator,
                             final AsyncCallback<? super Void> callback) {
        if(iterator.hasNext()) {
            iterator.next().apply(null, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(Void result) {
                    executeNext(iterator, callback);
                }
            });
        } else {
            callback.onSuccess(null);
        }
    }
}
