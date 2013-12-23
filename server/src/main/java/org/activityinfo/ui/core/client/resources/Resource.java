package org.activityinfo.ui.core.client.resources;

import org.activityinfo.ui.core.client.model.Model;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Resources store and make {@link Model}s available. 
 * 
 * @param M the Model class
 */
public abstract class Resource<M> {

    /**
     * Retrieves the model, using a local cached copy if available, 
     * fetching from the server if necessary.
     * 
     * @param callback
     */
    public abstract void get(AsyncCallback<M> callback);
    
    /**
     * Retrieves the model directly from the server, updating the local
     * copy if successful.
     * 
     * @param callback
     */
    public abstract void forceRefresh(AsyncCallback<M> callback);
}
