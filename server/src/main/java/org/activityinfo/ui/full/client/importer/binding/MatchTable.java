package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;

import java.util.Map;

/**
 * Spike for a join table that will ultimately have to
 * live on the server for most use cases.
 */
public class MatchTable {

    private Map<Integer, ScoredReference> matches = Maps.newHashMap();

    public Cuid getMatchedInstanceId(int rowIndex) {
        ScoredReference reference = matches.get(rowIndex);
        if(reference != null) {
            return reference.getInstanceId();
        }
        return null;
    }

    public void setMatch(int rowIndex, ScoredReference apply) {
        matches.put(rowIndex, apply);
    }

    public ScoredReference getMatch(int rowIndex) {
        return matches.get(rowIndex);
    }
}
