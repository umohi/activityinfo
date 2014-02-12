package org.activityinfo.ui.full.client.importer.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.ui.full.client.importer.binding.ReferenceMatch;

import java.util.List;
import java.util.Map;

/**
 * Intermediate structure for a Field value that has been matched/parsed but
 * may still require input or validation from user.
 */
public class DraftFieldValue {
    private Map<Cuid, ReferenceMatch> matches;

    public void setMatches(List<ReferenceMatch> matches) {
        this.matches = Maps.newHashMap();
        for(ReferenceMatch match : matches) {
            this.matches.put(match.getInstance().getId(), match);
        }
    }

    public ReferenceMatch getMatch(Cuid instanceId) {
        Preconditions.checkState(matches != null, "Matching has not been done yet!");
        return matches.get(instanceId);
    }
}
