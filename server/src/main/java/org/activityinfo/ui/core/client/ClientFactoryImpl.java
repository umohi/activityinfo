package org.activityinfo.ui.core.client;

import org.activityinfo.ui.core.client.model.ModelFactory;
import org.activityinfo.ui.core.client.places.DesktopPlaceHistoryMapper;
import org.activityinfo.ui.core.client.resources.DatabaseIndex;
import org.activityinfo.ui.core.client.storage.Cache;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.storage.client.Storage;
import com.google.web.bindery.event.shared.EventBus;

public class ClientFactoryImpl implements ClientFactory {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private PlaceHistoryMapper placeHistoryMapper = GWT.create(DesktopPlaceHistoryMapper.class);
    private ModelFactory beanFactory = GWT.create(ModelFactory.class);
    private AuthenticationController authController = new AuthenticationController();
    
    private Cache cache = new Cache(authController);
    private DatabaseIndex databaseIndex;
    
    public ClientFactoryImpl() {
       
    }

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
    public DatabaseIndex getDatabaseIndex() {
        if(databaseIndex == null) {
            databaseIndex = new DatabaseIndex(beanFactory, cache);
        }
        return databaseIndex;
    }
}
