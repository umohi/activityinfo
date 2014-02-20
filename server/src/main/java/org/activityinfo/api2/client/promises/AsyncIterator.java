package org.activityinfo.api2.client.promises;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.api2.client.Promise;

import java.util.LinkedList;

/**
 * Asynchronous version of the Iterator interface.
 *
 * <p>Not sure this makes sense. May be too much.</p>
 */
public class AsyncIterator<T> {

    private final LinkedList<T> queue = Lists.newLinkedList();
    private boolean eof = false;
    private Promise<Optional<T>> outstanding;

    public Promise<Optional<T>> next() {
        Promise<Optional<T>> next = new Promise<>();
        if(queue.isEmpty()) {
            next.onSuccess(Optional.of(queue.removeFirst()));
        } else if(eof) {
            next.onSuccess(Optional.<T>absent());
        } else {
            this.outstanding = next;
        }
        return next;
    }

    public void onNext(T value) {
        if(outstanding != null) {
            outstanding.onSuccess(Optional.of(value));
            outstanding = null;
        } else {
            queue.add(value);
        }
    }

    public void onEndOfSequence() {
        if(outstanding != null) {
            outstanding.onSuccess(Optional.<T>absent());
            outstanding = null;
        }
        eof = true;
    }
}
