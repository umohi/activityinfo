package org.activityinfo.ui.core.client;



import org.activityinfo.ui.core.client.resources.DatabaseIndex;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();
   
    PlaceHistoryMapper getPlaceHistoryMapper();
    
    DatabaseIndex getDatabaseIndex();

}
