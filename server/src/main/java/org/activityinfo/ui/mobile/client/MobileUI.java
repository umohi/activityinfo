package org.activityinfo.ui.mobile.client;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.ClientFactoryImpl;
import org.activityinfo.ui.core.client.places.HomePlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.mvp.client.history.MGWTPlaceHistoryHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;

public class MobileUI implements EntryPoint {

    @Override
    public void onModuleLoad() {
        ViewPort viewPort = new MGWTSettings.ViewPort();
        viewPort.setTargetDensity(DENSITY.MEDIUM);
        viewPort.setUserScaleAble(false).setMinimumScale(1.0).setMinimumScale(1.0).setMaximumScale(1.0);

        MGWTSettings settings = new MGWTSettings();
        settings.setViewPort(viewPort);
        //settings.setIconUrl("logo.png");
        //settings.setAddGlosToIcon(true);
        settings.setFullscreen(true);
        settings.setPreventScrolling(true);

        MGWT.applySettings(settings);

        final ClientFactory clientFactory = new ClientFactoryImpl();

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        MobilePlaceHistoryMapper historyMapper = GWT.create(MobilePlaceHistoryMapper.class);

        AnimatableDisplay display = GWT.create(AnimatableDisplay.class);

        MobileActivityMapper appActivityMapper = new MobileActivityMapper(clientFactory);

        MobileAnimationMapper appAnimationMapper = new MobileAnimationMapper();

        AnimatingActivityManager activityManager = new AnimatingActivityManager(appActivityMapper, appAnimationMapper, clientFactory.getEventBus());

        activityManager.setDisplay(display);

        RootPanel.get().add(display);

        MGWTPlaceHistoryHandler historyHandler = new MGWTPlaceHistoryHandler(historyMapper, new MobileHistoryObserver());

        historyHandler.register(clientFactory.getPlaceController(), clientFactory.getEventBus(), new HomePlace());
        historyHandler.handleCurrentHistory();
    }

}
