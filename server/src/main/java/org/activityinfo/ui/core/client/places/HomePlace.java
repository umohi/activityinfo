package org.activityinfo.ui.core.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class HomePlace extends Place {
    
    public static final HomePlace INSTANCE = new HomePlace();

    
    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HomePlace;
    }

    public static class Tokenizer implements PlaceTokenizer<HomePlace> {

        @Override
        public HomePlace getPlace(String token) {
            return new HomePlace(); 
        }

        @Override
        public String getToken(HomePlace place) {
            return "";
        }
    }
}
