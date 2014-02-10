package org.activityinfo.ui.full.client.importer;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.binding.DraftModel;
import org.activityinfo.ui.full.client.importer.binding.InstanceMatch;
import org.activityinfo.ui.full.client.importer.data.ImportRow;
import org.activityinfo.ui.full.client.importer.data.ImportSource;
import org.activityinfo.api2.shared.form.tree.FormTree.SearchOrder;

import java.util.*;

/**
 * A model which defines the mapping from an {@code ImportSource}
 * to a list of models of class {@code T}
 */
public class Importer<T> {

    private ImportSource source;
    private List<DraftModel> models;

    private FormTree formTree;


    /**
     * Defines the binding of property path
     */
    private Map<Integer, FieldPath> bindings = Maps.newHashMap();
    private Map<FieldPath, Object> providedValues = Maps.newHashMap();


    public Importer(FormTree formTree) {
        this.formTree = formTree;

    }

    public void setSource(ImportSource source) {
        this.source = source;

        // clear calculations based on this source
        this.bindings = Maps.newHashMap();
        this.models = null;
    }

    public List<DraftModel> getDraftModels() {
        if (models == null) {
            models = Lists.newArrayListWithCapacity(source.getRows().size());
            for (int i = 0; i != source.getRows().size(); ++i) {
                models.add(new DraftModel(i));
            }
        }
        return models;
    }

    public void updateDrafts() {
        String[] propertyKeys = new String[source.getColumns().size()];
        for (int i = 0; i != propertyKeys.length; ++i) {
            FieldPath bound = bindings.get(i);
            if (bound != null) {
                propertyKeys[i] = bound.getKey();
            }
        }

        List<FieldPath> objectProperties = getObjectPropertiesToResolve();

        for (DraftModel draftModel : getDraftModels()) {

            // first update the data type properties using the column mappings

            ImportRow row = source.getRows().get(draftModel.getRowIndex());
            for (int i = 0; i != propertyKeys.length; ++i) {
                String key = propertyKeys[i];
                if (key != null) {
                    draftModel.setValue(key, row.getColumnValue(i));
                }
            }

            // Now, try to match the object properties based on the given data type properties
            for (FieldPath path : objectProperties) {
                InstanceMatch match = matchInstance(path.getField().getRange(),
                        propertiesFor(path, draftModel));

                if (match != null) {
                    draftModel.setValue(path.getKey(), match);
                }
            }
        }
    }

    private InstanceMatch matchInstance(Set<Iri> range, Map<Cuid, Object> stringObjectMap) {
        throw new UnsupportedOperationException("todo");
    }

    private Map<Cuid, Object> propertiesFor(FieldPath path, DraftModel draftModel) {
        Map<Cuid, Object> values = Maps.newHashMap();
        for (FormTree.Node childNode : formTree.getNodeByPath(path).getChildren()) {
            Object value = draftModel.getValue(childNode.getPath().getKey());
            if (value instanceof InstanceMatch) {
                values.put(childNode.getFieldId(), ((InstanceMatch) value).getInstanceId());
            } else if (value != null) {
                values.put(childNode.getFieldId(), value);
            }
        }
        return values;
    }

    /**
     * Rebinds all draft models to their columns
     */
    public List<T> bind() {
        List<T> models = Lists.newArrayList();
//		for(DraftModel<T> draftModel : getDraftModels()) {
//			T model = binder.newModel();
//			for(Map.Entry<DataTypeProperty<T, ?>, ColumnBinding> binding : bindings.entrySet() ) {
//				String importedValue = binding.getValue().getValue(draftModel.getRowIndex());
//				if(importedValue != null) {
//					binding.getKey().tryConvertAndUpdate(model, importedValue);
//				}
//			}
//			models.add(model);
//		}	
        return models;
    }


    public ImportSource getSource() {
        return source;
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
}
