package org.activityinfo.ui.core.client;



import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();
   
    PlaceHistoryMapper getPlaceHistoryMapper();
    
    ActivityInfoService getService();

}
