package org.activityinfo.api2.client.function;

import com.google.common.base.Function;
import com.google.gwt.core.client.Scheduler;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.shared.function.BiFunction;

import java.util.Iterator;

/**
 * Consumes all of the items in a list without blocking the main UI thread.
 */
public class NonBlockingMapConsumer<T> extends BiFunction<Function<T, Void>, Iterable<T>, Promise<Void>> {

    private final Scheduler scheduler;

    public NonBlockingMapConsumer(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Promise<Void> apply(final Function<T, Void> consumer, Iterable<T> items) {
        final Promise<Void> result = new Promise<>();
        final Iterator<T> iterator = items.iterator();
        scheduler.scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if(iterator.hasNext()) {
                    try {
                        consumer.apply(iterator.next());
                        return true;
                    } catch(Throwable caught) {
                        result.reject(caught);
                        return false;
                    }
                } else {
                    result.onSuccess(null);
                    return false;
                }
            }
        });
        return result;
    }
}
