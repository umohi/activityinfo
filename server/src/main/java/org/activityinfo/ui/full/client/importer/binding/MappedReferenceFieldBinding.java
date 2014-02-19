package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.MatchKey;

import java.util.List;
import java.util.Map;

/**
 * Imports a reference field which has been mapped to one or more source columns.
 */
public class MappedReferenceFieldBinding implements FieldBinding {

    private final FormTree.Node fieldNode;
    private final Cuid fieldId;
    private final Criteria range;
    private final List<MatchField> matchFields = Lists.newArrayList();

    private final MatchTable matchTable;

    public MappedReferenceFieldBinding(FormTree.Node node, Map<Integer, FieldPath> columnBindings) {
        this.fieldNode = node;
        this.fieldId = node.getFieldId();

        this.range = ClassCriteria.union(fieldNode.getField().getRange());

        for(Map.Entry<Integer, FieldPath> binding : columnBindings.entrySet()) {
            if(binding.getValue().isDescendantOf(fieldId)) {
                // the column index of this field's value in the source
                int sourceColumn = binding.getKey();

                // get the path of the nested field relative to the referenced instance
                FieldPath relativePath = binding.getValue().relativeTo(fieldId);

                matchFields.add(new MatchField(relativePath, sourceColumn));
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

    private MatchKey matchKey(SourceRow row) {
        String[] values = new String[matchFields.size()];
        for(int i=0;i!=values.length;++i) {
            values[i] = matchFields.get(i).getImportedValue(row);
        }
        return new MatchKey(values);
    }

    public Criteria getRange() {
        return range;
    }

    public MatchTable getMatchTable() {
        return matchTable;
    }

    public List<MatchField> getMatchFields() {
        return matchFields;
    }
}
