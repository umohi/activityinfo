package org.activityinfo.api2.client;

/**
 * A function which executes asynchronously.
 */
public interface AsyncFunction <F, T>  {

    Promise<T> apply(@javax.annotation.Nullable F f);

}
