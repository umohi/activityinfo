package org.activityinfo.ui.mobile.client;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.places.DatabasePlace;
import org.activityinfo.ui.core.client.places.HomePlace;
import org.activityinfo.ui.mobile.client.database.DatabaseActivity;
import org.activityinfo.ui.mobile.client.home.MobileHomeActivity;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class MobileActivityMapper implements ActivityMapper {

    private MobileHomeActivity homeActivity;

    private ClientFactory clientFactory;

    public MobileActivityMapper(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if(place instanceof HomePlace) {
            if(homeActivity == null) {
                homeActivity = new MobileHomeActivity(clientFactory);
            }
            return homeActivity;
        } else if(place instanceof DatabasePlace) {
            return new DatabaseActivity();
        }
        return null;
    }
}
