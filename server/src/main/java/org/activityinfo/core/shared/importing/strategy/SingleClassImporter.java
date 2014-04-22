package org.activityinfo.core.shared.importing.strategy;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.importing.match.names.LatinPlaceNameScorer;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.fp.client.Promise;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class SingleClassImporter implements FieldImporter {

    public static final double MINIMUM_SCORE = 0.5;

    private Cuid rangeClassId;
    private Cuid fieldId;

    /**
     * List of columns to match against name properties of potential reference matches.
     */
    private List<ColumnAccessor> sources;

    private List<FieldImporterColumn> fieldImporterColumns = Lists.newArrayList();

    private int numColumns;

    private final LatinPlaceNameScorer scorer = new LatinPlaceNameScorer();


    /**
     * The list of nested text fields to match against, mapped to the
     * index of the column they are to be matched against.
     */
    private Map<FieldPath, Integer> referenceFields;

    private List<Cuid> referenceInstanceIds;
    private List<String[]> referenceValues;


    public SingleClassImporter(Cuid rangeClassId,
                               List<ColumnAccessor> sourceColumns,
                               Map<FieldPath, Integer> referenceFields,
                               List<FieldImporterColumn> fieldImporterColumns,
                               Cuid fieldId) {
        this.rangeClassId = rangeClassId;
        this.sources = sourceColumns;
        this.numColumns = sources.size();
        this.referenceFields = referenceFields;
        this.fieldImporterColumns = fieldImporterColumns;
        this.fieldId = fieldId;
    }

    public Promise<Void> prepare(ResourceLocator locator, List<SourceRow> batch) {

        InstanceQuery query = new InstanceQuery(
                Lists.newArrayList(referenceFields.keySet()),
                new ClassCriteria(rangeClassId));
        return locator.query(query).then(new Function<List<Projection>, Void>() {
            @Nullable
            @Override
            public Void apply(List<Projection> projections) {
                referenceInstanceIds = Lists.newArrayList();
                referenceValues = Lists.newArrayList();

                for(Projection projection : projections) {
                    referenceInstanceIds.add(projection.getRootInstanceId());
                    referenceValues.add(toArray(projection));
                }
                return null;
            }
        });
    }

    private String[] toArray(Projection projection) {
        String[] values = new String[sources.size()];
        for(Map.Entry<FieldPath, Object> entry : projection.getValueMap().entrySet()) {
            Integer index = referenceFields.get(entry.getKey());
            if(index != null) {
                Object value = entry.getValue();
                if(value instanceof String) {
                    values[index] = (String)value;
                }
            }
        }
        return values;
    }

    private String[] toArray(SourceRow row) {
        String[] values = new String[sources.size()];
        for(int i=0;i!=sources.size();++i) {
            if(!sources.get(i).isMissing(row)) {
                values[i] = sources.get(i).getValue(row);
            }
        }
        return values;
    }

    @Override
    public void validateInstance(SourceRow row, List<ValidationResult> results) {

        double bestScore = 0;
        double bestScores[] = new double[sources.size()];
        int bestMatchIndex = -1;

        String[] imported = toArray(row);

        if(imported != null) {
            for(int i=0;i!=referenceInstanceIds.size();++i) {
                double[] score = scorePotentialMatch(imported, referenceValues.get(i));
                if(score != null) {
                    double total = sum(score);
                    if(total > bestScore) {
                        bestMatchIndex = i;
                        bestScore = total;
                        bestScores = score;
                    }
                }
            }
        }

        for(int i=0;i!=numColumns;++i) {
            if(imported[i] == null) {
                results.add(ValidationResult.MISSING);
            } else if(bestMatchIndex == -1) {
                results.add(ValidationResult.error("No match"));
            } else {
                String matched = referenceValues.get(bestMatchIndex)[i];
                final ValidationResult converted = ValidationResult.converted(matched, bestScores[i]);
                converted.setInstanceId(referenceInstanceIds.get(bestMatchIndex));
                results.add(converted);
            }
        }
    }

    private double sum(double[] score) {
        double sum = 0;
        for(int i=0;i!=score.length;++i) {
            sum += score[i];
        }
        return sum;
    }

    private double[] scorePotentialMatch(String[] imported, String[] reference) {
        double scores[] = new double[imported.length];
        double max = 0;
        for(int i=0;i!=imported.length;++i) {
            if(imported[i] != null && reference[i] != null) {
                scores[i] = scorer.score(imported[i], reference[i]);
                max = Math.max(scores[i], max);
            }
        }
        if(max > MINIMUM_SCORE) {
            return scores;
        } else {
            return null;
        }
    }

    @Override
    public boolean updateInstance(SourceRow row, FormInstance instance) {
        // root
        final List<ValidationResult> validationResults = Lists.newArrayList();
        validateInstance(row, validationResults);
        for (ValidationResult result : validationResults) {
            if (result.shouldPersist() && result.getInstanceId() != null) {
                instance.set(fieldId, result.getInstanceId());
            }
        }

        // nested data
        // todo ???
        return false;
    }

    @Override
    public List<FieldImporterColumn> getColumns() {
        return fieldImporterColumns;
    }
}
