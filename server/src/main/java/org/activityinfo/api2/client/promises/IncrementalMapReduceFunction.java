package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Pair;

import java.util.Iterator;

/**
 * Executes a set of work items in chunks to avoid blocking the UI thread.
 *
 * @param <InputT> The type of the collection item
 * @param <MapT> The result type of the map function
 * @param <ReduceT> The result type of the reduce function
 */
public class IncrementalMapReduceFunction<InputT, MapT, ReduceT>
        implements AsyncFunction<Pair<
                ReduceFunction<? super MapT, ReduceT>, Pair<
                Function<InputT, MapT>,
                Iterable<InputT> > >, ReduceT> {

    private final Scheduler scheduler;

    public IncrementalMapReduceFunction(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void apply(Pair<
                ReduceFunction<? super MapT, ReduceT>, Pair<
                Function<InputT, MapT>,
                Iterable<InputT>>> arguments, final AsyncCallback<ReduceT> callback) {

        final Function<InputT, MapT> mapFunction = arguments.getB().getA();
        final ReduceFunction<? super MapT, ReduceT> reduceFunction = arguments.getA();
        Iterable<InputT> input = arguments.getB().getB();

        final Iterator<InputT> iterator = input.iterator();
        reduceFunction.init(null);
        scheduler.scheduleIncremental(new Scheduler.RepeatingCommand() {

            @Override
            public boolean execute() {
                if(iterator.hasNext()) {
                    try {
                        MapT value = mapFunction.apply(iterator.next());
                        reduceFunction.update(value);
                        return true;
                    } catch(Throwable caught) {
                        callback.onFailure(caught);
                        return false;
                    }
                } else {
                    callback.onSuccess(reduceFunction.reduce());
                    return false;
                }
            }
        });
    }

}
