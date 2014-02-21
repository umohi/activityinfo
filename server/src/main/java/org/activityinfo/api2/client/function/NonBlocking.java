package org.activityinfo.api2.client.function;

import com.google.gwt.core.client.Scheduler;

/**
 * Created by alex on 2/21/14.
 */
public class NonBlocking {

    public static Scheduler scheduler = Scheduler.get();


    public static <T> NonBlockingMapConsumer<T> consumer() {
        return new NonBlockingMapConsumer<>(scheduler);
    }

}
