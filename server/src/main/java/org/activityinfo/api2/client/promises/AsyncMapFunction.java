package org.activityinfo.api2.client.promises;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import java.util.Iterator;
import java.util.List;

/**
 * Sequentially transforms a list of inputs into outputs using a provided
 * Asynchronous transformation
 */
class AsyncMapFunction<F, T> implements AsyncFunction<Iterable<F>, List<T>> {

    private final AsyncFunction<F, T> transformation;

    public AsyncMapFunction(AsyncFunction<F, T> transformation) {
        this.transformation = transformation;
    }

    @Override
    public void apply(Iterable<F> input, AsyncCallback<List<T>> callback) {
        List<T> output = Lists.newArrayList();
        matchNext(input.iterator(), output, callback);
    }

    private void matchNext(final Iterator<F> iterator, final List<T> output,
                           final AsyncCallback<? super List<T>> callback) {

        if(!iterator.hasNext()) {
            callback.onSuccess(output);
        } else {
            try {
                transformation.apply(iterator.next(), new AsyncCallback<T>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(T result) {
                        output.add(result);
                        matchNext(iterator, output, callback);
                    }
                });
            } catch(Throwable caught) {
                callback.onFailure(caught);
            }
        }
    }
}
