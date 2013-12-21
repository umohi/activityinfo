package org.activityinfo.ui.core.client.data;

import java.util.List;

import org.activityinfo.ui.core.client.ActivityInfoService;
import org.activityinfo.ui.core.client.model.DatabaseModel;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class DatabaseListProvider extends AsyncDataProvider<DatabaseModel> {

    private ActivityInfoService service;

    public DatabaseListProvider(ActivityInfoService service) {
        this.service = service;
    }

    @Override
    protected void onRangeChanged(final HasData<DatabaseModel> display) {
        service.getDatabases(new MethodCallback<List<DatabaseModel>>() {
            
            @Override
            public void onSuccess(Method method, List<DatabaseModel> response) {
                display.setRowData(0, response);
            }
            
            @Override
            public void onFailure(Method method, Throwable exception) {
                // TODO Auto-generated method stub
                
            }
        });
    }
}
