package org.activityinfo.ui.desktop.client.database;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class DatabasePlace extends Place {
    
    private final int databaseId;

    public DatabasePlace(int databaseId) {
        super();
        this.databaseId = databaseId;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + databaseId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabasePlace other = (DatabasePlace) obj;
        if (databaseId != other.databaseId)
            return false;
        return true;
    }
    
    public static class Tokenizer implements PlaceTokenizer<DatabasePlace> {

        @Override
        public DatabasePlace getPlace(String token) {
            int databaseId = Integer.parseInt(token);
            return new DatabasePlace(databaseId);
        }

        @Override
        public String getToken(DatabasePlace place) {
            return Integer.toString(place.getDatabaseId());
        }
    }
}
