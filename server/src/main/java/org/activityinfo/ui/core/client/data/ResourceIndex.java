package org.activityinfo.ui.core.client.data;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Base class for indexes of resources.
 * 
 * <p>Indexes are used whenever we need to maintain a list of 
 * resources 
 */
public abstract class ResourceIndex<T> {

    public abstract void get(AsyncCallback<IndexResult<T>> result);
    
    public abstract void forceRefresh(AsyncCallback<IndexResult<T>> result);
}
