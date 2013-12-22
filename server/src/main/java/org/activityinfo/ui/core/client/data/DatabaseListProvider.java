package org.activityinfo.ui.core.client.data;

import java.util.List;

import org.activityinfo.ui.core.client.ActivityInfoService;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class DatabaseListProvider extends AsyncDataProvider<DatabaseItem> {

    private ActivityInfoService service;

    public DatabaseListProvider(ActivityInfoService service) {
        this.service = service;
    }

    @Override
    protected void onRangeChanged(final HasData<DatabaseItem> display) {
        service.getDatabases(new MethodCallback<List<DatabaseItem>>() {
            
            @Override
            public void onSuccess(Method method, List<DatabaseItem> response) {
                display.setRowData(0, response);
            }
            
            @Override
            public void onFailure(Method method, Throwable exception) {
                // TODO Auto-generated method stub
                
            }
        });
    }
}
