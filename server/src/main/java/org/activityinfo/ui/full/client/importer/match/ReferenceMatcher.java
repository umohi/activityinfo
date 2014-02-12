package org.activityinfo.ui.full.client.importer.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.draft.DraftFieldValue;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;

import java.util.List;
import java.util.Set;

/**
 * Matches a {@code FormInstance} to a REFERENCE field using nested fields of the
 * references mapped to the import source.
 */
public class ReferenceMatcher {

    public static final double MINIMUM_SCORE = 0.50;
    private final Cuid referenceFieldId;
    private final FieldPath referenceFieldPath;
    private final List<FieldPath> nestedFieldPaths = Lists.newArrayList();
    private final List<FieldPath> relativeFieldPaths = Lists.newArrayList();

    private Criteria range;

    public ReferenceMatcher(FormTree.Node referenceFieldNode, Set<FieldPath> mappedFields) {
        Preconditions.checkArgument(!referenceFieldNode.getPath().isNested());

        this.referenceFieldId = referenceFieldNode.getFieldId();
        this.referenceFieldPath = referenceFieldNode.getPath();
        this.range = ClassCriteria.union(referenceFieldNode.getRange());


        for(FieldPath mappedField : mappedFields) {
            if(mappedField.isDescendantOf(referenceFieldId)) {
                nestedFieldPaths.add(mappedField);
                relativeFieldPaths.add(mappedField.relativeTo(referenceFieldId));
            }
        }
    }

    /**
     *
     * @return the range of the REFERENCE field to match against
     */
    public Criteria getRange() {
        return range;
    }

    public List<FieldPath> getProjectedFields() {
        return relativeFieldPaths;
    }

    public void match(List<DraftInstance> instances, List<Projection> projections) {
        for(DraftInstance instance : instances) {
            match(instance, projections);
        }
    }

    private void match(DraftInstance instance, List<Projection> projections) {

        String[] imported = importedValues(instance);

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
        if(best == null) {
            updateInstanceWithNoMatch(instance);
        } else {
            updateInstanceWithMatch(instance, best);
        }
    }

    private void updateInstanceWithMatch(DraftInstance instance, ScoredReference match) {
        instance.getField(referenceFieldPath).setMatchedValue(match.getInstanceId());
        for(int i=0;i!=nestedFieldPaths.size();++i) {
            DraftFieldValue fieldValue = instance.getField(nestedFieldPaths.get(i));
            fieldValue.setMatchScore(match.getScore(i));
            fieldValue.setMatchedValue(match.getProjection().getStringValue(relativeFieldPaths.get(i)));
        }
    }

    private void updateInstanceWithNoMatch(DraftInstance instance) {
        instance.getField(referenceFieldPath).setMatchedValue(null);
        for(int i=0;i!=nestedFieldPaths.size();++i) {
            DraftFieldValue fieldValue = instance.getField(nestedFieldPaths.get(i));
            fieldValue.setMatchScore(0);
            fieldValue.setMatchedValue(null);
        }
    }

    private double[] scorePotentialMatch(String[] imported, Projection projection) {
        double scores[] = new double[imported.length];
        double max = 0;
        for(int i=0;i!=imported.length;++i) {
            if(imported != null) {
                String referenceValue = projection.getStringValue(relativeFieldPaths.get(i));
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

    private String[] importedValues(DraftInstance instance) {
        String[] fields = new String[nestedFieldPaths.size()];
        for(int i=0;i!=fields.length;++i) {
            Object value = instance.getField(nestedFieldPaths.get(i)).getImportedValue();
            fields[i] = value == null ? null : value.toString();
        }
        return fields;
    }
}
