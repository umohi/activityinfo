package org.activityinfo.ui.full.client.importer;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.binding.*;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.api2.shared.form.tree.FormTree.SearchOrder;

import java.util.*;

/**
 * A model which defines the mapping from an {@code SourceTable}
 * to a list of models of class {@code T}
 */
public class ImportModel<T> {

    private SourceTable source;
    private FormTree formTree;


    /**
     * Defines the binding of property path
     */
    private Map<Integer, FieldPath> bindings = Maps.newHashMap();
    private Map<FieldPath, Object> providedValues = Maps.newHashMap();


    public ImportModel(FormTree formTree) {
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


    public FormClass getFormClass() {
        return getFormTree().getRootFormClass();
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

    private Set<FieldPath> mappedReferenceFields() {
        Set<FieldPath> referenceFields = Sets.newHashSet();

        for(FieldPath mappedPath : getMappedFieldPaths()) {
            if(mappedPath.isNested()) {
                referenceFields.add(new FieldPath(mappedPath.getRoot()));
            }
        }
        return referenceFields;
    }


    public List<FieldBinding> createFieldBindings() {
        List<FieldBinding> fieldImporters = Lists.newArrayList();

        // Add Reference fields that have been mapped
        for(FieldPath referenceFieldPath : mappedReferenceFields()) {
            fieldImporters.add(new MappedReferenceFieldBinding(
                    formTree.getNodeByPath(referenceFieldPath),
                    getColumnBindings()));
        }

        // Add simple data fields that have been mapped
        for(Map.Entry<Integer, FieldPath> binding : getColumnBindings().entrySet()) {
            if(!binding.getValue().isNested()) {
                FormTree.Node fieldNode = formTree.getNodeByPath(binding.getValue());
                fieldImporters.add(new MappedDataFieldBinding(fieldNode, binding.getKey()));
            }
        }

        // Finally add any missing fields
        Set<Cuid> mappedRootFields = Sets.newHashSet();
        for(FieldPath mappedPath : getColumnBindings().values()) {
            mappedRootFields.add(mappedPath.getRoot());
        }

        for(FormTree.Node node : formTree.getRoot().getChildren()) {
            if(!mappedRootFields.contains(node.getFieldId()) && node.getField().isRequired()) {
                fieldImporters.add(new MissingFieldBinding(node));
            }
        }

        return fieldImporters;
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

}
