package org.activityinfo.core.shared.importing.strategy;

import com.google.common.collect.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.criteria.FormClassSet;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by alex on 4/4/14.
*/
public class SingleClassTargetBuilder {

    private final FormTree.Node rootField;


    /**
     * Maps a unique field id to all the FieldPaths within this reference tree
     */
    private Multimap<Cuid, FieldPath> fieldPathMap = HashMultimap.create();

    private List<ImportTarget> targets = Lists.newArrayList();

    private Map<TargetSiteId, ImportTarget> targetMap = Maps.newHashMap();

    public SingleClassTargetBuilder(FormTree.Node referenceField) {
        rootField = referenceField;
        collectTargetFields(referenceField);
    }

    private void collectTargetFields(FormTree.Node referenceField) {
        for(FormTree.Node child : referenceField.getChildren()) {
            if(child.getFieldType() == FormFieldType.FREE_TEXT) {
                addTargetField(child);
            } else if(child.getFieldType() == FormFieldType.REFERENCE) {
                collectTargetFields(child);
            }
        }
    }

    private void addTargetField(FormTree.Node dataField) {
        if(!fieldPathMap.containsKey(dataField.getFieldId())) {
            targets.add(target(dataField));
        }
        fieldPathMap.put(dataField.getFieldId(), dataField.getPath().relativeTo(rootField.getFieldId()));

    }

    private ImportTarget target(FormTree.Node dataField) {
        TargetSiteId site = new TargetSiteId(dataField.getFieldId().asString());
        return new ImportTarget(rootField.getFieldId(), site, label(dataField));
    }

    public List<ImportTarget> getTargets() {
        return targets;
    }

    private String label(FormTree.Node fieldNode) {
        return fieldNode.getDefiningFormClass().getLabel().getValue() + " " +
               fieldNode.getField().getLabel().getValue();
    }


    public SingleClassImporter newImporter(Map<TargetSiteId, ColumnAccessor> mappings) {
        List<ColumnAccessor> sourceColumns = Lists.newArrayList();
        Map<FieldPath, Integer> referenceValues = pathMap(mappings, sourceColumns);
        List<FieldImporterColumn> fieldImporterColumns = fieldImporterColumnsColumns(mappings);

        Cuid rangeClassId = FormClassSet.of(rootField.getRange()).unique();

        return new SingleClassImporter(rangeClassId, sourceColumns, referenceValues, fieldImporterColumns);
    }

    private List<FieldImporterColumn> fieldImporterColumnsColumns(Map<TargetSiteId, ColumnAccessor> mappings) {
        List<FieldImporterColumn> columns = Lists.newArrayList();
        for(Map.Entry<TargetSiteId, ColumnAccessor> entry : mappings.entrySet()) {
            columns.add(new FieldImporterColumn(targetMap.get(entry.getKey()), entry.getValue()));
        }
        return columns;
    }

    private Map<FieldPath, Integer> pathMap(Map<TargetSiteId, ColumnAccessor> mappings, List<ColumnAccessor> sourceColumns) {
        Map<FieldPath, Integer> referenceValues = new HashMap<>();
        List<FieldImporterColumn> importerColumns = Lists.newArrayList();

        int columnIndex = 0;
        for(Map.Entry<TargetSiteId, ColumnAccessor> entry : mappings.entrySet()) {

            ImportTarget target = targetMap.get(entry.getKey());
            ColumnAccessor source = entry.getValue();

            importerColumns.add(new FieldImporterColumn(target, source));

            sourceColumns.add(entry.getValue());

            Cuid fieldId = new Cuid(entry.getKey().asString());
            for(FieldPath path : fieldPathMap.get(fieldId)) {
                referenceValues.put(path, columnIndex);
            }

            columnIndex++;
        }
        return referenceValues;
    }
}
