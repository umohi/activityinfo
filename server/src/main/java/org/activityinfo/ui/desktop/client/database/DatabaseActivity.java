package org.activityinfo.ui.desktop.client.database;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.model.SchemaModel;
import org.activityinfo.ui.core.client.places.DatabasePlace;
import org.activityinfo.ui.desktop.client.widget.list.ResourcePanel;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DatabaseActivity extends AbstractActivity {
    
    private final ClientFactory clientFactory;
    private DatabasePlace place;
    
    public DatabaseActivity(ClientFactory clientFactory, DatabasePlace place) {
        super();
        this.clientFactory = clientFactory;
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        ResourcePanel<SchemaModel> resourcePanel = new ResourcePanel<SchemaModel>(
            clientFactory.getDatabaseIndex().getSchema(place.getDatabaseId()), new DatabaseView());
        panel.setWidget(resourcePanel);
    }
    
}
