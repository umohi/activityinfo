package org.activityinfo.api2.client.promises;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;

import java.util.Iterator;

/**
 * Executes a set of work items in chunks to avoid blocking the UI thread
 */
public class NonBlockingSynchronousMapFunction<T> implements AsyncFunction<Iterable<T>, Void> {

    private final Scheduler scheduler;
    private final Action<T> action;

    public NonBlockingSynchronousMapFunction(Scheduler scheduler, Action<T> action) {
        this.scheduler = scheduler;
        this.action = action;
    }

    @Override
    public void apply(Iterable<T> input, final AsyncCallback<Void> callback) {
        final Iterator<T> iterator = input.iterator();
        scheduler.scheduleIncremental(new Scheduler.RepeatingCommand() {

            @Override
            public boolean execute() {
                if(iterator.hasNext()) {
                    try {
                        action.apply(iterator.next());
                        return true;
                    } catch(Throwable caught) {
                        callback.onFailure(caught);
                        return false;
                    }
                } else {
                    callback.onSuccess(null);
                    return false;
                }
            }
        });
    }
}
