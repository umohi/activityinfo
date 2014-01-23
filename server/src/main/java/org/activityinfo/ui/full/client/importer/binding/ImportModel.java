package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.ui.full.client.importer.data.ImportRow;
import org.activityinfo.ui.full.client.importer.data.ImportSource;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;
import org.activityinfo.ui.full.client.importer.ont.PropertyTree;
import org.activityinfo.ui.full.client.importer.ont.PropertyTree.SearchOrder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A model which defines the mapping from an {@code ImportSource}
 * to a list of models of class {@code T}
 */
public class ImportModel<T> {

    private InstanceImporter binder;
    private ImportSource source;
    private List<DraftModel> models;

    private PropertyTree propertyTree;


    /**
     * Defines the binding of property path
     */
    private Map<Integer, PropertyPath> bindings = Maps.newHashMap();


    public ImportModel(InstanceImporter binder) {
        this.binder = binder;
        this.propertyTree = new PropertyTree(binder.getOntClass(), binder);

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
            PropertyPath bound = bindings.get(i);
            if (bound != null) {
                propertyKeys[i] = bound.getKey();
            }
        }

        List<PropertyPath> objectProperties = getObjectPropertiesToResolve();

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
            for (PropertyPath path : objectProperties) {
                InstanceMatch match = binder.matchInstance(path.asObjectProperty().getRange(),
                        propertiesFor(path, draftModel));

                if (match != null) {
                    draftModel.setValue(path.getKey(), match);
                }
            }
        }
    }

    private Map<String, Object> propertiesFor(PropertyPath path, DraftModel draftModel) {
        Map<String, Object> values = Maps.newHashMap();
        for (PropertyTree.Node childNode : propertyTree.getNodeByPath(path).getChildren()) {
            Object value = draftModel.getValue(childNode.getPath().getKey());
            if (value instanceof InstanceMatch) {
                values.put(childNode.getPropertyId(), ((InstanceMatch) value).getInstanceId());
            } else if (value != null) {
                values.put(childNode.getPropertyId(), value);
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

    public InstanceImporter getBinder() {
        return binder;
    }

    public Map<Integer, PropertyPath> getColumnBindings() {
        return bindings;
    }

    public void setColumnBinding(PropertyPath property, Integer columnIndex) {
        // for now, a property may be assigned to only one column
        Iterator<Map.Entry<Integer, PropertyPath>> it = bindings.entrySet().iterator();
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

    public List<PropertyPath> getDataTypePropertiesToMatch() {
        return propertyTree.search(SearchOrder.BREADTH_FIRST,
                // descend if...
                PropertyTree.pathNotIn(binder.getProvidedValues().keySet()),
                // match if...
                Predicates.and(
                        PropertyTree.isDataTypeProperty(),
                        PropertyTree.pathNotIn(binder.getProvidedValues().keySet())));
    }

    public List<PropertyPath> getObjectPropertiesToResolve() {
        return propertyTree.search(SearchOrder.DEPTH_FIRST,
                // descend if...
                PropertyTree.pathNotIn(binder.getProvidedValues().keySet()),
                // match if...
                Predicates.and(
                        PropertyTree.isObjectProperty(),
                        PropertyTree.pathNotIn(binder.getProvidedValues().keySet())));
    }

    public List<PropertyPath> getPropertiesToValidate() {
        return propertyTree.search(SearchOrder.BREADTH_FIRST,
                // descend if...
                PropertyTree.pathNotIn(binder.getProvidedValues().keySet()),
                // match if...
                Predicates.and(
                        PropertyTree.pathNotIn(binder.getProvidedValues().keySet()),
                        Predicates.or(
                                PropertyTree.isObjectProperty(),
                                PropertyTree.pathIn(bindings.values()))));
    }
}
