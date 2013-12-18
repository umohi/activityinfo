package org.activityinfo.ui.desktop.client;

import org.activityinfo.ui.core.client.ActivityInfoService;
import org.activityinfo.ui.desktop.client.database.DatabaseActivity;
import org.activityinfo.ui.desktop.client.database.DatabasePlace;
import org.activityinfo.ui.desktop.client.home.HomeActivity;
import org.activityinfo.ui.desktop.client.home.HomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

/**
 * Maps a place (URL) to a GWT Activity
 *
 */
public class AppActivityMapper implements ActivityMapper {

    private final ClientFactory clientFactory;
    
    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if(place instanceof HomePlace) {
            return new HomeActivity(clientFactory); 
        } else if(place instanceof DatabasePlace) {
            return new DatabaseActivity();
        } else {
            return new HomeActivity(clientFactory);
        }
    }

}
