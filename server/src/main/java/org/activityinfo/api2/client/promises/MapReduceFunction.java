package org.activityinfo.api2.client.promises;

import com.google.common.base.Function;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Pair;

/**
 * Convenience definition for Map Reduce Functions
 */
public interface MapReduceFunction<T, F, G> extends AsyncFunction<
        Pair<
                ReduceFunction<? super F, G>,
                Pair<
                        AsyncFunction<T, F>,
                        Iterable<T> > >, G> {


}
