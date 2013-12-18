package org.activityinfo.ui.desktop.client.database;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DatabaseActivity extends AbstractActivity {
    
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        DatabaseView view = new DatabaseView();
    }
    
    
}
