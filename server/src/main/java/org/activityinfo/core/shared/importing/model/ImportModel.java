package org.activityinfo.core.shared.importing.model;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.form.tree.FormTree.SearchOrder;
import org.activityinfo.ui.client.component.importDialog.data.SourceColumn;
import org.activityinfo.ui.client.component.importDialog.data.SourceTable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        return columnBindings.get(columnIndex);
    }

    public SourceColumn getSourceColumn(int columnIndex) {
        return source.getColumns().get(columnIndex);
    }

    public ColumnTarget getColumnBinding(SourceColumn column) {
        return getColumnBinding(column.getIndex());
    }
}
