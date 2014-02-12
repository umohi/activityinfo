package org.activityinfo.api2.client.promises;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.client.Promise;

import java.util.Iterator;
import java.util.List;

/**
 * Sequentially transforms a list of inputs into outputs using a provided
 * Asynchronous function
 */
public class AsyncMapFunction<F, T> implements AsyncFunction<Iterable<F>, List<T>> {

    private final AsyncFunction<F, T> transformation;

    public AsyncMapFunction(AsyncFunction<F, T> transformation) {
        this.transformation = transformation;
    }


    @Override
    public Promise<List<T>> apply(final Iterable<F> input) {
        Promise<List<T>> promisedOutput = new Promise<>(new Promise.AsyncOperation<List<T>>() {
            @Override
            public void start(Promise<List<T>> promise) {
                new Transformation(input.iterator(), promise);
            }
        });
        return promisedOutput;
    }

    public static <F,T> Promise<List<T>> transform(List<F> inputList, AsyncFunction<F, T> transformation) {
        return new AsyncMapFunction<F, T>(transformation).apply(inputList);
    }

    private class Transformation {
        private Iterator<F> inputIterator;
        private Promise<List<T>> promisedOutput;
        private List<T> output = Lists.newArrayList();

        private Transformation(Iterator<F> sourceIterator, Promise<List<T>> promisedOutput) {
            this.promisedOutput = promisedOutput;
            this.inputIterator = sourceIterator;
            matchNext();
        }

        private void matchNext() {

            if(!inputIterator.hasNext()) {
                promisedOutput.resolve(output);
            } else {
                try {
                    transformation.apply(inputIterator.next()).then(new AsyncCallback<T>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            promisedOutput.reject(caught);
                        }

                        @Override
                        public void onSuccess(T result) {
                            output.add(result);
                            matchNext();
                        }
                    });
                } catch(Throwable caught) {
                    promisedOutput.reject(caught);
                }
            }
        }
    }
}
