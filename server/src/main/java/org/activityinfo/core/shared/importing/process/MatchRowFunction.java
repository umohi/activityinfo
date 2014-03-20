package org.activityinfo.core.shared.importing.process;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.binding.MatchFieldBinding;
import org.activityinfo.core.shared.importing.match.ScoredReference;
import org.activityinfo.fp.shared.BiFunction;

import java.util.List;

/**
 * Matches a SourceRow against a list of Potential matches and returns the
 * best match.
 */
public class MatchRowFunction extends BiFunction<List<Projection>, SourceRow, ScoredReference> {

    private static final double MINIMUM_SCORE = 0.5;

    private final List<MatchFieldBinding> matchBindings;

    public MatchRowFunction(List<MatchFieldBinding> matchBindings) {
        this.matchBindings = matchBindings;
    }

    @Override
    public ScoredReference apply(List<Projection> projections, SourceRow sourceRow) {
        Object[] imported = importedValues(sourceRow);

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

    private Object[] importedValues(SourceRow row) {
        Object[] fields = new Object[matchBindings.size()];
        for(int i=0;i!=fields.length;++i) {
            fields[i] = matchBindings.get(i).getImportedValue(row);
        }
        return fields;
    }

    private double[] scorePotentialMatch(Object[] imported, Projection projection) {
        double scores[] = new double[imported.length];
        double max = 0;
        for(int i=0;i!=imported.length;++i) {
            if(imported[i] != null) {
                MatchFieldBinding binding = matchBindings.get(i);
                Object referenceValue = binding.getReferencedValue(projection);
                if(referenceValue != null) {
                    double score = binding.getScorer().score(imported[i], referenceValue);
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
