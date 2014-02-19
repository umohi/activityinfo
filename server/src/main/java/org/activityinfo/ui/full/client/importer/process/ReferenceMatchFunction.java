package org.activityinfo.ui.full.client.importer.process;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.api2.client.promises.Action;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.ui.full.client.importer.binding.MatchField;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.draft.DraftFieldValue;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.JaroWinklerDistance;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Matches a SourceRow against a list of Potential matches and returns the
 * best match.
 */
public class ReferenceMatchFunction implements Function<SourceRow, ScoredReference> {

    private static final double MINIMUM_SCORE = 0.5;

    private final List<MatchField> matchFields;
    private final List<Projection> projections;

    public ReferenceMatchFunction(List<MatchField> matchFields, List<Projection> projections) {
        this.matchFields = matchFields;
        this.projections = projections;
    }

    @Override
    public ScoredReference apply(SourceRow input) {
        String[] imported = importedValues(input);

        List<ScoredReference> potentialMatches = Lists.newArrayList();
        for(Projection projection : projections) {
            double[] scores = scorePotentialMatch(imported, projection);
            if(scores != null) {
                potentialMatches.add(new ScoredReference(projection, scores));
            }
        }

        // find the best projection using a simple sum of the scores
        ScoredReference best = null;
        double bestScore = 0;
        for(ScoredReference match : potentialMatches) {
            double total = match.sum();
            if(total > bestScore) {
                best = match;
                bestScore = total;
            }
        }
        return best;
    }

    private String[] importedValues(SourceRow row) {
        String[] fields = new String[matchFields.size()];
        for(int i=0;i!=fields.length;++i) {
            fields[i] = matchFields.get(i).getImportedValue(row);
        }
        return fields;
    }

    private double[] scorePotentialMatch(String[] imported, Projection projection) {
        double scores[] = new double[imported.length];
        double max = 0;
        for(int i=0;i!=imported.length;++i) {
            if(imported != null) {
                String referenceValue = projection.getStringValue(matchFields.get(i).getRelativeFieldPath());
                if(referenceValue != null) {
                    double score = JaroWinklerDistance.DEFAULT.getDistance(imported[i], referenceValue);
                    max = Math.max(score, max);
                    scores[i] = score;
                }
            }
        }
        if(max > MINIMUM_SCORE) {
            return scores;
        } else {
            return null;
        }
    }
}
