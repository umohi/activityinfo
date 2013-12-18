package org.activityinfo.ui.desktop.client;

import org.activityinfo.ui.desktop.client.database.DatabasePlace;
import org.activityinfo.ui.desktop.client.home.HomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * Maps a history token (URL) to a Place object. Generated
 * by the framework based on the WithTokenizers attribute.
 */
@WithTokenizers({
    HomePlace.Tokenizer.class,
    DatabasePlace.Tokenizer.class
    })
public interface DesktopPlaceHistoryMapper extends PlaceHistoryMapper {

}
