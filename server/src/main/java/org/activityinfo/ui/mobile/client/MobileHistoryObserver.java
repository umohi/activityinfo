package org.activityinfo.ui.mobile.client;

import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.mvp.client.history.HistoryHandler;
import com.googlecode.mgwt.mvp.client.history.HistoryObserver;
import com.googlecode.mgwt.ui.client.util.NoopHandlerRegistration;

public class MobileHistoryObserver implements HistoryObserver {

    @Override
    public void onPlaceChange(Place place, HistoryHandler handler) {
        
    }

    @Override
    public void onHistoryChanged(Place place, HistoryHandler handler) {        
    }

    @Override
    public void onAppStarted(Place place, HistoryHandler historyHandler) {
        
    }

    @Override
    public HandlerRegistration bind(EventBus eventBus, HistoryHandler historyHandler) {
        return new NoopHandlerRegistration();
    }

}
