package org.activityinfo.core.shared.importing.strategy;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.criteria.FormClassSet;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 5/19/14.
 */
public class HierarchyClassImporter implements FieldImporter {

    private final FormTree.Node rootField;
    private final Map<FieldPath, Integer> referenceFields;
    private final List<ColumnAccessor> sourceColumns;
    private final List<FieldImporterColumn> fieldImporterColumns;
    private final Map<Cuid, List<Cuid>> referenceInstanceIds = Maps.newHashMap(); // range to reference instance ids
    private final Map<Cuid, List<String[]>> referenceValues = Maps.newHashMap(); // range to reference values

    public HierarchyClassImporter(FormTree.Node rootField,
                                  List<ColumnAccessor> sourceColumns,
                                  Map<FieldPath, Integer> referenceFields,
                                  List<FieldImporterColumn> fieldImporterColumns) {
        this.rootField = rootField;
        this.sourceColumns = sourceColumns;
        this.referenceFields = referenceFields;
        this.fieldImporterColumns = fieldImporterColumns;
    }

    @Override
    public Promise<Void> prepare(ResourceLocator locator, List<SourceRow> batch) {
        final List<Promise<Void>> promises = Lists.newArrayList();
        for (final Cuid range : FormClassSet.of(rootField.getRange()).getElements()) {
            InstanceQuery query = new InstanceQuery(Lists.newArrayList(referenceFields.keySet()), new ClassCriteria(range));
            final Promise<List<Projection>> promise = locator.query(query);
            promise.then(new Function<List<Projection>, Void>() {
                @Override
                public Void apply(List<Projection> projections) {
                    final List<Cuid> instanceCuidList = Lists.newArrayList();
                    final List<String[]> referenceValueList = Lists.newArrayList();

                    for (Projection projection : projections) {
                        instanceCuidList.add(projection.getRootInstanceId());
                        referenceValueList.add(SingleClassImporter.toArray(projection, referenceFields, sourceColumns.size()));
                    }
                    referenceInstanceIds.put(range, instanceCuidList);
                    referenceValues.put(range, referenceValueList);
                    return null;
                }
            });
        }

        return Promise.waitAll(promises);
    }


    @Override
    public void validateInstance(SourceRow row, List<ValidationResult> results) {
        ValidationResult[] tempResult = new ValidationResult[sourceColumns.size()];

        for (final Cuid range : FormClassSet.of(rootField.getRange()).getElements()) {
            InstanceScorer instanceScorer = new InstanceScorer(referenceValues.get(range), referenceInstanceIds.get(range), sourceColumns);
            final InstanceScorer.Score score = instanceScorer.score(row);
            final int bestMatchIndex = score.getBestMatchIndex();

            for (int i = 0; i != sourceColumns.size(); ++i) {
                ValidationResult currentResult = tempResult[i];
                if (currentResult != null && currentResult.getState() == ValidationResult.State.OK) {
                    continue;
                }

                if (score.getImported()[i] == null && shouldOverride(currentResult)) {
                    tempResult[i] = (ValidationResult.MISSING);
                } else if (bestMatchIndex == -1 && shouldOverride(currentResult)) {
                    tempResult[i] = ValidationResult.error("No match");
                } else {
                    // if not confidence or confidence is greater then existing one then override result
                    double confidence = score.getBestScores()[i];
                    if (confidence > InstanceScorer.MINIMUM_SCORE) {

                        if (currentResult == null || currentResult.getState() != ValidationResult.State.CONFIDENCE || currentResult.getConfidence() < confidence ||
                                tempResult[i].getRangeInstanceIds().get(range) == null) {
                            String matched = referenceValues.get(range).get(bestMatchIndex)[i];
                            final ValidationResult converted = ValidationResult.converted(matched, score.getBestScores()[i]);
                            converted.getRangeInstanceIds().put(range, Sets.newHashSet(referenceInstanceIds.get(range).get(bestMatchIndex)));
                            tempResult[i] = converted;
                        } else {

                            // if ok add cuid
                            tempResult[i].getRangeInstanceIds().get(range).add(referenceInstanceIds.get(range).get(bestMatchIndex));
                        }
                    }
                }
            }
        }

        for (ValidationResult currentResult : tempResult) {
            if (currentResult != null) {
                results.add(currentResult);
            }
        }

    }

    private boolean shouldOverride(ValidationResult currentResult) {
        return currentResult == null ||
                currentResult.getState() == ValidationResult.State.ERROR || currentResult.getState() == ValidationResult.State.MISSING;
    }

    @Override
    public boolean updateInstance(SourceRow row, FormInstance instance) {
        final List<ValidationResult> validationResults = Lists.newArrayList();
        validateInstance(row, validationResults);
        for (ValidationResult result : validationResults) {
            if (result.shouldPersist() && !result.getRangeInstanceIds().isEmpty()) {
                final Set<Cuid> toSave = Sets.newHashSet();
                for (Map.Entry<Cuid, Set<Cuid>> entry : result.getRangeInstanceIds().entrySet()) {
                    final Set<Cuid> valueSet = entry.getValue();
                    for (Cuid value : valueSet) {
                        if (value.getDomain() == CuidAdapter.ADMIN_LEVEL_DOMAIN) {
//                        final int levelId = CuidAdapter.getBlock(cuid, 0);
//                        final int fieldIndex = CuidAdapter.getBlock(cuid, 0);
                            // todo !!! exclude redundant information
                            toSave.add(value);
                        } else {
                            toSave.add(value);
                        }
                    }

                }

                instance.set(rootField.getFieldId(), toSave);
            }
        }
        return false;
    }

    @Override
    public List<FieldImporterColumn> getColumns() {
        return fieldImporterColumns;
    }
}
