package org.activityinfo.ui.desktop.client;

import org.activityinfo.ui.core.client.ActivityInfoService;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;

public class ClientFactoryImpl implements ClientFactory {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private PlaceHistoryMapper placeHistoryMapper = GWT.create(DesktopPlaceHistoryMapper.class);

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public PlaceHistoryMapper getPlaceHistoryMapper() {
        return placeHistoryMapper;
    }

    @Override
    public ActivityInfoService getService() {
        Resource resource = new Resource( "/resources" );

        ActivityInfoService service = GWT.create(ActivityInfoService.class);
        ((RestServiceProxy)service).setResource(resource);
        
        return service;
    }
}
