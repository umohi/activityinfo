package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.collect.Lists;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.converter.StringConverter;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.names.LatinPlaceNameScorer;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;

import java.util.List;
import java.util.Map;

/**
 * Imports a reference field which has been mapped to one or more source columns.
 *
 */
public class MappedReferenceFieldBinding implements FieldBinding {

    private final FormTree.Node fieldNode;
    private final Cuid fieldId;
    private final Criteria range;
    private final List<MatchFieldBinding> matchFields = Lists.newArrayList();

    private final MatchTable matchTable;

    public MappedReferenceFieldBinding(FormTree.Node node, Map<Integer, ColumnTarget> columnBindings) {
        this.fieldNode = node;
        this.fieldId = node.getFieldId();

        this.range = ClassCriteria.union(fieldNode.getField().getRange());

        int matchIndex = 0;
        for(Map.Entry<Integer, ColumnTarget> binding : columnBindings.entrySet()) {
            if(binding.getValue().isMapped()) {
                if(binding.getValue().getFieldPath().isDescendantOf(fieldId)) {

                    // the column index of this field's value in the source
                    int sourceColumn = binding.getKey();

                    // get the path of the nested field relative to the referenced instance
                    FieldPath relativePath = binding.getValue().getFieldPath().relativeTo(fieldId);

                    matchFields.add(new MatchFieldBinding(matchIndex++,
                            node.findDescendant(relativePath),
                            relativePath, sourceColumn,
                            new StringConverter(), new LatinPlaceNameScorer()));
                }
            }
        }

        this.matchTable = new MatchTable();
    }

    @Override
    public Cuid getFieldId() {
        return fieldId;
    }

    @Override
    public FormField getField() {
        return fieldNode.getField();
    }

    @Override
    public Cuid getFieldValue(SourceRow row) {
        return matchTable.getMatchedInstanceId(row.getRowIndex());
    }

    @Override
    public void accept(FieldBindingColumnVisitor visitor) {
        for(MatchFieldBinding field : matchFields) {
            visitor.visitMatchColumn(this, field);
        }
    }

    public Criteria getRange() {
        return range;
    }

    public MatchTable getMatchTable() {
        return matchTable;
    }

    public List<MatchFieldBinding> getMatchFields() {
        return matchFields;
    }

    public InstanceQuery queryPotentialMatches() {
        final List<FieldPath> paths = Lists.newArrayList();
        for(MatchFieldBinding matchField : getMatchFields()) {
            paths.add(matchField.getRelativeFieldPath());
        }

        return new InstanceQuery(paths, getRange());
    }
}
