package org.activityinfo.ui.mobile.client;

import org.activityinfo.ui.core.client.places.DatabasePlace;
import org.activityinfo.ui.core.client.places.HomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({
    HomePlace.Tokenizer.class,
    DatabasePlace.Tokenizer.class
    })
public interface MobilePlaceHistoryMapper extends PlaceHistoryMapper {

}
