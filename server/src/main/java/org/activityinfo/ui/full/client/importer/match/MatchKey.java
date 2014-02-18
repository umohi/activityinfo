package org.activityinfo.ui.full.client.importer.match;

import java.util.Arrays;

/**
 * Created by alex on 2/19/14.
 */
public class MatchKey {
    private String[] values;
    private int hashCode;

    public MatchKey(String[] values) {
        this.values = values;
        this.hashCode = Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MatchKey)) {
            return false;
        }
        MatchKey other = (MatchKey)obj;
        return other.hashCode == this.hashCode &&
                Arrays.equals(other.values, this.values);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
