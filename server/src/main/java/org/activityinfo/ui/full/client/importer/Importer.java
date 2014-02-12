package org.activityinfo.ui.full.client.importer;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.columns.DataColumn;
import org.activityinfo.ui.full.client.importer.columns.DraftColumn;
import org.activityinfo.ui.full.client.importer.columns.MissingFieldColumn;
import org.activityinfo.ui.full.client.importer.columns.NestedFieldColumn;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.api2.shared.form.tree.FormTree.SearchOrder;

import java.util.*;

/**
 * A model which defines the mapping from an {@code SourceTable}
 * to a list of models of class {@code T}
 */
public class Importer<T> {

    private SourceTable source;
    private FormTree formTree;


    /**
     * Defines the binding of property path
     */
    private Map<Integer, FieldPath> bindings = Maps.newHashMap();
    private Map<FieldPath, Object> providedValues = Maps.newHashMap();


    public Importer(FormTree formTree) {
        this.formTree = formTree;
    }

    public void setSource(SourceTable source) {
        this.source = source;

        // clear calculations based on this source
        this.bindings = Maps.newHashMap();
    }

    public SourceTable getSource() {
        return source;
    }


    public Set<FieldPath> getMappedFieldPaths() {
        return Sets.newHashSet(bindings.values());
    }

    public Map<Integer, FieldPath> getColumnBindings() {
        return bindings;
    }

    public void setColumnBinding(FieldPath property, Integer columnIndex) {
        // for now, a property may be assigned to only one column
        Iterator<Map.Entry<Integer, FieldPath>> it = bindings.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(property)) {
                it.remove();
            }
        }
        bindings.put(columnIndex, property);
    }


    public void clearColumnBinding(Integer columnIndex) {
        bindings.remove(columnIndex);
    }

    public List<DraftColumn> getImportColumns() {
        List<DraftColumn> columns = Lists.newArrayList();

        Set<FieldPath> mapped = Sets.newHashSet(getColumnBindings().values());

        for(FieldPath path : mapped) {
            if(path.isNested()) {
                columns.add(new NestedFieldColumn(formTree.getNodeByPath(path)));
            } else {
                columns.add(new DataColumn(formTree.getNodeByPath(path)));
            }
        }

        Set<Cuid> mappedRootField = Sets.newHashSet();
        for(FieldPath fieldPath : mapped) {
            mappedRootField.add(fieldPath.getRoot());
        }
        for(FormTree.Node node : formTree.getRoot().getChildren()) {
            if(!mappedRootField.contains(node.getFieldId()) && node.getField().isRequired()) {
                columns.add(new MissingFieldColumn(node.getField()));
            }
        }

        return columns;
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

    public List<FieldPath> getObjectPropertiesToResolve() {
        return formTree.search(SearchOrder.DEPTH_FIRST,
                // descend if...
                FormTree.pathNotIn(providedValues.keySet()),
                // match if...
                Predicates.and(
                        FormTree.isReference(),
                        FormTree.pathNotIn(providedValues.keySet())));
    }

    public List<FieldPath> getPropertiesToValidate() {
        return formTree.search(SearchOrder.BREADTH_FIRST,
                // descend if...
                FormTree.pathNotIn(providedValues.keySet()),
                // match if...
                Predicates.and(
                        FormTree.pathNotIn(providedValues.keySet()),
                        Predicates.or(
                                FormTree.isReference(),
                                FormTree.pathIn(bindings.values()))));
    }

    public FormTree getFormTree() {
        return formTree;
    }

}
