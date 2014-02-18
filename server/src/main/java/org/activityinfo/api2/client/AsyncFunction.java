package org.activityinfo.api2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A function which executes asynchronously.
 */
public interface AsyncFunction <F, T>  {

    void apply(@javax.annotation.Nullable F f, AsyncCallback<T> callback);

}
