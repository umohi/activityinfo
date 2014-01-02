package org.activityinfo.ui.core.client.resources;

import java.util.Date;

import org.activityinfo.ui.core.client.model.ModelFactory;
import org.activityinfo.ui.core.client.model.SchemaModel;
import org.activityinfo.ui.core.client.storage.Cache;

import com.google.common.base.Strings;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class SchemaResource extends Resource<SchemaModel> {
    
    private final int databaseId;
    private final Cache cache;
    private final ModelFactory modelFactory;

    public SchemaResource(int databaseId, ModelFactory modelFactory, Cache cache) {
        super();
        this.databaseId = databaseId;
        this.modelFactory = modelFactory;
        this.cache = cache;
    }

    @Override 
    public void get(AsyncCallback<SchemaModel> callback) {
        String cached = cache.getUserStore().getItem(cacheKey());
        if(Strings.isNullOrEmpty(cached)) {
            forceRefresh(callback);            
        } else {
            SchemaModel schema = decode(cached);
            schema.setLastSyncedTime(Long.parseLong(cache.getUserStore().getItem(cacheTimeKey())));
            callback.onSuccess(schema);
        }
    }

    @Override
    public void forceRefresh(final AsyncCallback<SchemaModel> callback) {
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, "/resources/database/" + databaseId + "/schema");
        request.setCallback(new RequestCallback() {
            
            @Override
            public void onResponseReceived(Request request, Response response) {
                try {
                    // record the time we got fresh data from the server
                    long syncTime = new Date().getTime();

                    cache.getUserStore().setItem(cacheKey(), response.getText());
                    cache.getUserStore().setItem(cacheTimeKey(), Long.toString(syncTime));

                    // build entity and return
                    SchemaModel model = decode(response.getText()); 
                    model.setLastSyncedTime(syncTime);
                    
                    callback.onSuccess(model);
                    
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

    protected String cacheTimeKey() {
        return "db." + databaseId + ".lastSynced";
    }

    protected String cacheKey() {
        return "db." + databaseId;
    }

    protected SchemaModel decode(String json) {
        return AutoBeanCodex.decode(modelFactory, SchemaModel.class, json).as();
    }
}
