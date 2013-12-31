package org.activityinfo.ui.desktop.client;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.places.HomePlace;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The container and main EntryPoint for the full Desktop version of 
 * the ActivityInfo UI.
 *
 */
public class Desktop extends Composite implements EntryPoint {
 
    @UiField
    SimplePanel main;
    
    @UiField Bootstrap b;

    private static DesktopUiBinder uiBinder = GWT
            .create(DesktopUiBinder.class);

    interface DesktopUiBinder extends UiBinder<Widget, Desktop> {
    }

    public Desktop() {
    }

    public void onModuleLoad() {
        
        Bootstrap.INSTANCE.ensureInjected();
        
        ClientFactory factory = GWT.create(ClientFactory.class);
        
        initWidget(uiBinder.createAndBindUi(this));

        ActivityMapper activityMapper = new DesktopActivityMapper(factory);
        
        ActivityManager activityManager = new ActivityManager(activityMapper, factory.getEventBus());
        activityManager.setDisplay(main);
        
        PlaceHistoryMapper mapper = factory.getPlaceHistoryMapper();
        final PlaceHistoryHandler handler = new PlaceHistoryHandler(mapper);
        handler.register(factory.getPlaceController(), factory.getEventBus(), new HomePlace());

        RootPanel.get("content").add(this);

        factory.getPlaceController().goTo(new HomePlace());
        
//        
//
//        String token = History.getToken();
//        if(!token.equals("")) {
//            History.newItem("");
//            History.newItem(token);
//        }
    }

}