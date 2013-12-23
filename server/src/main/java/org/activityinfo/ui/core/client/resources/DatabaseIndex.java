package org.activityinfo.ui.core.client.resources;

import java.util.Date;

import org.activityinfo.ui.core.client.model.ModelFactory;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.core.client.model.DatabaseItemList;
import org.activityinfo.ui.core.client.model.ModelList;
import org.activityinfo.ui.core.client.storage.KeyValueStorage;

import com.google.common.base.Strings;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Index of the user's personal databases. Currently, this 
 * is the collection of databases to which the user has 
 * been explicitly granted access.
 */
public class DatabaseIndex extends Resource<ModelList<DatabaseItem>> {

    private static final String CACHE_KEY = "my.databases";
    
    private static final long CACHE_MAX_AGE = 60 * 60 * 1000; 
    
    private KeyValueStorage storage;
    private ModelFactory modelFactory;
    
    public DatabaseIndex(ModelFactory modelFactory, KeyValueStorage storage) {
        super();
        this.storage = storage;
        this.modelFactory = modelFactory;
    }

    @Override
    public void get(AsyncCallback<ModelList<DatabaseItem>> callback) {
        String cached = storage.getItem(CACHE_KEY);
        if(!Strings.isNullOrEmpty(cached)) {
            AutoBean<DatabaseItemList> list = AutoBeanCodex.decode(modelFactory, DatabaseItemList.class, cached);
            callback.onSuccess(list.as());
        } else {
            forceRefresh(callback);
        }
    }
    
    
    @Override
    public void forceRefresh(final AsyncCallback<ModelList<DatabaseItem>> callback) {
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, "/resources/databases");
        request.setCallback(new RequestCallback() {
            
            @Override
            public void onResponseReceived(Request request, Response response) {
                try {
                    String json = "{ \"lastSyncedTime\":" + new Date().getTime() + ", \"items\": " + response.getText() + "}";
                    storage.setItem(CACHE_KEY, json);
                    AutoBean<DatabaseItemList> list = AutoBeanCodex.decode(modelFactory, DatabaseItemList.class, json);
                    callback.onSuccess(list.as());            
                } catch(Exception parseException) {
                    callback.onFailure(parseException);
                }
            }
  
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onFailure(exception);
            }
        });
        try {
            request.send();
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }    
    
    public SchemaResource getSchema(int databaseId) {
        return new SchemaResource(databaseId, modelFactory, storage);
    }
}
