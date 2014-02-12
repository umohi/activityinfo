package org.activityinfo.ui.full.client.importer.match;

import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.Cuid;

/**
 * Describes a potential match for a reference field
*/
public class ScoredReference {
    private final Projection projection;
    private final double[] scores;

    public ScoredReference(Projection projection, double[] scores) {
        this.projection = projection;
        this.scores = scores;
    }

    /**
     *
     * @return the instance id of the potential match
     */
    public Cuid getInstanceId() {
        return projection.getRootInstanceId();
    }

    public Projection getProjection() {
        return projection;
    }

    /**
     *
     * @return the score for this match on the {@code i}th dimension. Higher scores are better matches.
     */
    public double getScore(int i) {
        return scores[i];
    }

    /**
     *
     * @return a simple sum of the scores along all dimensions
     */
    public double sum() {
        double sum = 0;
        for(int i=0;i!=scores.length;++i) {
            sum += scores[i];
        }
        return sum;
    }

}
