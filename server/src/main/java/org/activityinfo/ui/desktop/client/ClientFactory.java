package org.activityinfo.ui.desktop.client;


import org.activityinfo.ui.core.client.ActivityInfoService;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();
   
    PlaceHistoryMapper getPlaceHistoryMapper();
    
    ActivityInfoService getService();

}
