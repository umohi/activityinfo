package org.activityinfo.ui.desktop.client;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.places.DatabasePlace;
import org.activityinfo.ui.core.client.places.HomePlace;
import org.activityinfo.ui.desktop.client.database.DatabaseActivity;
import org.activityinfo.ui.desktop.client.home.HomeActivity;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

/**
 * Maps a place (URL) to a GWT Activity
 *
 */
public class DesktopActivityMapper implements ActivityMapper {

    private final ClientFactory clientFactory;
    
    public DesktopActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if(place instanceof HomePlace) {    
            return new HomeActivity(clientFactory); 
        } else if(place instanceof DatabasePlace) {
            return new DatabaseActivity(clientFactory, (DatabasePlace)place);
        } else {
            return new HomeActivity(clientFactory);
        }
    }

}
