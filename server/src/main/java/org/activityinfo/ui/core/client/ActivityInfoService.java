package org.activityinfo.ui.core.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface ActivityInfoService extends RestService {

    @GET
    @Path("/databases")
    public void getDatabases(MethodCallback<List<DatabaseItem>> databases);
    
}
