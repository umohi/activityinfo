package org.activityinfo.ui.full.client.importer.model;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.api2.shared.model.CoordinateAxis;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.api2.shared.form.tree.FormTree.SearchOrder;

import java.util.*;

/**
 * A model which defines the mapping from an {@code SourceTable}
 * to a FormClass
 */
public class ImportModel {

    private SourceTable source;
    private FormTree formTree;


    /**
     * Defines the binding of property path
     */
    private Map<Integer, ColumnTarget> columnBindings = Maps.newHashMap();
    private Map<FieldPath, Object> providedValues = Maps.newHashMap();


    public ImportModel(FormTree formTree) {
        this.formTree = formTree;
    }

    public void setSource(SourceTable source) {
        this.source = source;

        // clear calculations based on this source
        this.columnBindings = Maps.newHashMap();
    }

    public SourceTable getSource() {
        return source;
    }


    public FormClass getFormClass() {
        return getFormTree().getRootFormClass();
    }

    public Set<ColumnTarget> getMappedFieldPaths() {
        return Sets.newHashSet(columnBindings.values());
    }

    public Map<Integer, ColumnTarget> getColumnBindings() {
        return columnBindings;
    }

    public void setColumnBinding(ColumnTarget action, Integer columnIndex) {

        Iterator<Map.Entry<Integer, ColumnTarget>> it = columnBindings.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(action)) {
                it.remove();
            }
        }
        columnBindings.put(columnIndex, action);
    }

    /**
     *
     * @return all Fields that have been mapped to a column to import
     */
    public Set<FieldPath> getMappedFields() {
        Set<FieldPath> paths = Sets.newHashSet();

        for(ColumnTarget action : columnBindings.values()) {
            if(action.isMapped()) {
                paths.add(action.getFieldPath());
            }
        }
        return paths;
    }

    public Set<Cuid> getMappedRootFields() {
        Set<Cuid> mappedRootFields = Sets.newHashSet();
        for(FieldPath mappedPath : getMappedFields()) {
            mappedRootFields.add(mappedPath.getRoot());
        }
        return mappedRootFields;
    }

    /**
     *
     * @return all reference Fields that have been mapped to a column to import
     */
    public Set<FieldPath> getMappedReferenceFields() {
        Set<FieldPath> referenceFields = Sets.newHashSet();
        for(FieldPath path : getMappedFields()) {
            if(path.isNested()) {
                referenceFields.add(new FieldPath(path.getRoot()));
            }
        }
        return referenceFields;
    }

    private String composeLabel(FormTree.Node node) {
        if(node.getPath().isNested()) {
            return node.getParent().getField().getLabel().getValue() + " " + node.getField().getLabel().getValue();
        } else {
            return node.getField().getLabel().getValue();
        }
    }


    public List<FieldPath> getFieldsToMatch() {
        return formTree.search(SearchOrder.BREADTH_FIRST,
                // descend if...
                FormTree.pathNotIn(providedValues.keySet()),
                // match if...
                Predicates.and(
                        FormTree.isDataTypeProperty(),
                        FormTree.pathNotIn(providedValues.keySet())));
    }



    public FormTree getFormTree() {
        return formTree;
    }

    public ColumnTarget getColumnBinding(int columnIndex) {
        ColumnTarget columnTarget = columnBindings.get(columnIndex);
        if(columnTarget == null) {
            return ColumnTarget.ignored();
        }
        return columnTarget;
    }

    public SourceColumn getSourceColumn(int columnIndex) {
        return source.getColumns().get(columnIndex);
    }
}
