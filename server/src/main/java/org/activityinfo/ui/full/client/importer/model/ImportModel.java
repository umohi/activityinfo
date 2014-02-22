package org.activityinfo.ui.full.client.importer.model;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.api2.client.CuidGenerator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.api2.shared.model.CoordinateAxis;
import org.activityinfo.ui.full.client.importer.binding.*;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
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
    private Map<Integer, ColumnAction> columnBindings = Maps.newHashMap();
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

    public Set<ColumnAction> getMappedFieldPaths() {
        return Sets.newHashSet(columnBindings.values());
    }

    public Map<Integer, ColumnAction> getColumnBindings() {
        return columnBindings;
    }

    public void setColumnBinding(ColumnAction action, Integer columnIndex) {

        Iterator<Map.Entry<Integer, ColumnAction>> it = columnBindings.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(action)) {
                it.remove();
            }
        }
        columnBindings.put(columnIndex, action);
    }


    public void clearColumnBinding(Integer columnIndex) {
        columnBindings.remove(columnIndex);
    }

    private Set<FieldPath> mappedFields() {
        Set<FieldPath> paths = Sets.newHashSet();

        for(ColumnAction action : columnBindings.values()) {
            if(action instanceof ImportExistingAction) {
                FieldPath path = ((ImportExistingAction) action).getFieldPath();
                paths.add(path);
            }
        }
        return paths;
    }

    private Set<FieldPath> mappedReferenceFields() {
        Set<FieldPath> referenceFields = Sets.newHashSet();
        for(FieldPath path : mappedFields()) {
            if(path.isNested()) {
                referenceFields.add(new FieldPath(path.getRoot()));
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

        // Add simple data fields that have been mapped, along with new fields
        for(Map.Entry<Integer, ColumnAction> binding : getColumnBindings().entrySet()) {
            if(binding.getValue() instanceof ImportExistingAction) {
                ImportExistingAction action = (ImportExistingAction) binding.getValue();
                if(!action.getFieldPath().isNested()) {
                    FormTree.Node fieldNode = formTree.getNodeByPath(action.getFieldPath());
                    fieldImporters.add(new MappedDataFieldBinding(fieldNode.getField(), binding.getKey()));
                }
            } else if(binding.getValue() instanceof ImportNewAction) {
                // create a new field
                SourceColumn column = source.getColumns().get(binding.getKey());
                FormField field = createNewField((ImportNewAction) binding.getValue(), column);
                MappedDataFieldBinding fieldBinding = new MappedDataFieldBinding(field, binding.getKey());
                fieldBinding.setNewField(true);
                fieldImporters.add(fieldBinding);
            }
        }

        // Finally add any missing fields
        Set<Cuid> mappedRootFields = Sets.newHashSet();
        for(FieldPath mappedPath : mappedFields()) {
            mappedRootFields.add(mappedPath.getRoot());
        }

        for(FormTree.Node node : formTree.getRoot().getChildren()) {
            if(!mappedRootFields.contains(node.getFieldId()) && node.getField().isRequired()) {
                fieldImporters.add(new MissingFieldBinding(node));
            }
        }

        return fieldImporters;
    }

    private FormField createNewField(ImportNewAction action, SourceColumn column) {
        CuidGenerator generator = new CuidGenerator();
        FormField field = new FormField(generator.nextCuid());
        field.setLabel(new LocalizedString(column.getHeader()));
        field.setRequired(false);
        field.setType(FormFieldType.FREE_TEXT);
        field.setVisible(true);
        return field;
    }

    /**
     *
     * @return a list of possible column actions for this particular FormClass
     */
    public List<ColumnAction> getColumnActions() {
        List<ColumnAction> actions = Lists.newArrayList();


        // ignore is always an option...
        actions.add(new IgnoreAction());

        // add existing options
        List<FieldPath> paths = getFieldsToMatch();
        for(FieldPath path : paths) {
            FormTree.Node node = formTree.getNodeByPath(path);
            if(node.getFieldType() == FormFieldType.GEOGRAPHIC_POINT) {
                actions.add(new ImportExistingCoordinateAction(node.getPath(), CoordinateAxis.LATITUDE));
                actions.add(new ImportExistingCoordinateAction(node.getPath(), CoordinateAxis.LONGITUDE));
            } else {
                actions.add(new ImportExistingAction(composeLabel(node), path));
            }
        }

        // and new fields can be added on the fly
        actions.add(new ImportNewAction());

        return actions;
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

}
