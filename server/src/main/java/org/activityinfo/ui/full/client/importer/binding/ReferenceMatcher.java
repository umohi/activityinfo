package org.activityinfo.ui.full.client.importer.binding;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.common.base.Function;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.JaroWinklerDistance;

import java.util.List;
import java.util.Map;

/**
 * Matches instances of a given FormClass
 */
public class ReferenceMatcher {
    private FormTree.Node node;
    private List<FieldMatcher> fieldMatchers = Lists.newArrayList();

    /**
     * The number of dimensions that this reference will be scored.
     * Equal to the number of data fields that have been mapped to import columns
     */
    private int dimensionCount = 0;


    public ReferenceMatcher(FormTree.Node node,
                            Map<FieldPath, Integer> fieldPathToColumn,
                            Map<FieldPath, ReferenceMatcher> nestedMatchers ) {
        this.node = node;

        for(FormTree.Node childNode : node.getChildren()) {
            if(childNode.isReference()) {
                if(nestedMatchers.containsKey(childNode.getPath())) {

                    // the nested matcher will run before us and provide us with a set of
                    // scores for the instanceIds in this field.

                    ReferenceMatcher nestedMatcher = nestedMatchers.get(childNode.getPath());

                    fieldMatchers.add(new ReferenceFieldMatcher(
                            childNode.getPath(),
                            nestedMatcher.getDimensionCount()));

                    dimensionCount += nestedMatcher.getDimensionCount();

                }

            } else {

                if(fieldPathToColumn.containsKey(childNode.getPath())) {
                    Cuid fieldId = childNode.getFieldId();
                    int columnIndex = fieldPathToColumn.get(childNode.getPath());
                    switch(childNode.getFieldType()) {
                        case FREE_TEXT:
                            fieldMatchers.add(new StringFieldMatcher(fieldId, columnIndex));
                            dimensionCount++;
                            break;
                    }
                }
            }
        }
    }

    public int getDimensionCount() {
        return dimensionCount;
    }

    /**
     *
     * @return true if this reference field has any mappings to imported columns
     */
    public boolean hasSourceMapping() {
        return !fieldMatchers.isEmpty();
    }

    public FieldPath getFieldPath() {
        return node.getPath();
    }

    public Cuid getFieldId() {
        return node.getFieldId();
    }

    public List<String> getValues(SourceRow source) {
        List<String> values = Lists.newArrayList();
        for(FieldMatcher stringFieldMatcher : fieldMatchers) {
            if(st)
            values.add(stringFieldMatcher.getValue(source));
        }
        for(ReferenceMatcher referenceMatcher : referenceFieldMatchers) {
            values.addAll(referenceMatcher.getValues(source));
        }
        return values;
    }

    private void collectSourceValues(FormTree node, SourceRow sourceRow, List<String> values) {
        for(FormTree.No
    }

    public List<String> getValues(FormInstance source) {
        List<String> values = Lists.newArrayList();
        for(FieldMatcher stringFieldMatcher : fieldMatchers) {
            values.add(stringFieldMatcher.getValue(source));
        }
        for(ReferenceMatcher referenceMatcher : referenceFieldMatchers) {
            values.addAll(referenceMatcher.getValues(source));
        }
        return values;
    }

    public Promise<List<ReferenceMatch>> match(ResourceLocator resourceLocator, final SourceRow row,
                                               final DraftInstance draftInstance) {

        return resourceLocator
                .queryInstances(new ClassCriteria(node.getFormClass().getId()))
                .then(new InstanceListScorer(instanceScorer, dimensionCount));

    }

    private class InstanceScorer implements Function<FormInstance, ReferenceMatch> {

        private final SourceRow row;
        private ReferenceFieldMatches referenceFieldMatches;

        private InstanceScorer(SourceRow row, DraftInstance draftInstance) {
            this.row = row;

            this.referenceFieldMatches = referenceFieldMatches;
        }

        @Override
        public ReferenceMatch apply(FormInstance formInstance) {
            double[] scores = new double[dimensionCount];
            int scoreIndex = 0;
            for(FieldMatcher matcher : fieldMatchers) {
                double[] fieldScores = matcher.score(row, formInstance);
            }

            for(int i=0;i!= referenceFieldMatchers.size();++i) {
                ReferenceMatcher fieldMatcher = referenceFieldMatchers.get(i);

                Cuid instanceId = formInstance.getInstanceId(fieldMatcher.getFieldId());
                ReferenceMatch match = referenceFieldMatches.getMatch(i, instanceId);
                if(match != null) {
                    double[] fieldScores = match.getScores();
                    System.arraycopy(fieldScores, 0, scores, scoreIndex, fieldScores.length);
                }
                scoreIndex += fieldMatcher.getDimensionCount();
            }

            return new ReferenceMatch(formInstance, scores);
        }
    }

    private static class InstanceListScorer implements Function<List<FormInstance>, List<ReferenceMatch>> {

        private final InstanceScorer instanceScorer;
        private int scoreCount;

        public InstanceListScorer(InstanceScorer instanceScorer, int scoreCount) {
            this.instanceScorer = instanceScorer;
            this.scoreCount = scoreCount;
        }

        @Override
        public List<ReferenceMatch> apply(List<FormInstance> formInstances) {
            List<ReferenceMatch> matches = Lists.newArrayList();
            for(FormInstance instance : formInstances) {
                ReferenceMatch match = instanceScorer.apply(instance);
                matches.add(match);
            }

            return matches;
        }
    }

    private interface FieldMatcher {

        double[] score(SourceRow row, FormInstance instance, DraftInstance draftInstance);


    }

    /**
     * Matches an imported column to a TEXT field of a set of {@code FormInstances}
     */
    private class StringFieldMatcher implements FieldMatcher {
        private final int columnIndex;
        private final Cuid fieldId;

        private StringFieldMatcher(Cuid fieldId, int columnIndex) {
            this.columnIndex = columnIndex;
            this.fieldId = fieldId;
        }

        @Override
        public double[] score(SourceRow row, FormInstance instance, DraftInstance draftInstance) {
            String importedValue = row.getColumnValue(columnIndex);
            String instanceValue = instance.getString(fieldId);

            return new double[] { JaroWinklerDistance.DEFAULT.getDistance(importedValue, instanceValue) };
        }

        private String getValue(SourceRow source) {
            return source.getColumnValue(columnIndex);
        }

        private String getValue(FormInstance source) {
            return source.getString(fieldId);
        }
    }

    /**
     * Uses the already-matched-nested reference field value to match the REFERENCE field.
     */
    private class ReferenceFieldMatcher implements FieldMatcher {

        private final FieldPath fieldPath;
        private int dimensionCount;

        private ReferenceFieldMatcher(FieldPath fieldPath, int dimensionCount) {
            this.fieldPath = fieldPath;
            this.dimensionCount = dimensionCount;
        }

        public double[] score(SourceRow row, FormInstance instance, DraftInstance draftInstance) {
            Cuid referencedInstanceId = instance.getInstanceId(fieldPath.getField().getId());
            ReferenceMatch match = draftInstance.getField(fieldPath).getMatch(referencedInstanceId);
            if(match == null) {
                return new double[dimensionCount];
            } else {
                return match.getScores();
            }
        }
    }
}
