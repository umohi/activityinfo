package org.activityinfo.api2.client.promises;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Pair;

import java.util.Iterator;

/**
 * Sequentially transforms a list of inputs into outputs using a provided
 * Asynchronous transformation
 */
public class AsyncMapReduceFunction<T, F, G> implements AsyncFunction<
        Pair<
            ReduceFunction<? super F, G>,
            Pair<
                AsyncFunction<T, F>,
                Iterable<T> > >, G> {

    @Override
    public void apply(Pair<ReduceFunction<? super F, G>, Pair<AsyncFunction<T, F>, Iterable<T>>> arguments, AsyncCallback<G> callback) {
        ReduceFunction<? super F, G> reduceFunction = arguments.getA();
        AsyncFunction<T, F> mapFunction = arguments.getB().getA();
        Iterable<T> input = arguments.getB().getB();

        reduceFunction.init(null);
        matchNext(input.iterator(), mapFunction, reduceFunction, callback);
    }

    private void matchNext(final Iterator<T> iterator,
                           final AsyncFunction<T, F> mapFunction,
                           final ReduceFunction<? super F, G> reduceFunction,
                           final AsyncCallback<G> callback) {

        if(!iterator.hasNext()) {
            callback.onSuccess(reduceFunction.reduce());
        } else {
            try {
                mapFunction.apply(iterator.next(), new AsyncCallback<F>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(F result) {
                        reduceFunction.update(result);
                        matchNext(iterator, mapFunction, reduceFunction, callback);
                    }
                });
            } catch(Throwable caught) {
                callback.onFailure(caught);
            }
        }
    }
}
