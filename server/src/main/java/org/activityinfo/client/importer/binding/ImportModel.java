package org.activityinfo.client.importer.binding;

import java.util.List;
import java.util.Map;

import org.activityinfo.client.importer.data.ImportSource;
import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.ModelBinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A model which defines the mapping from an {@code ImportSource}
 * to a list of models of class {@code T}
 */
public class ImportModel<T> {
	
	private ModelBinder<T> binder;
	private ImportSource source;
	private List<DraftModel<T>> models;
	
	
	/**
	 * Defines the binding of a property to an imported or user-provided column.
	 */
	private Map<DataTypeProperty<T, ?>, ColumnBinding> bindings = Maps.newHashMap();
	
	
	public ImportModel(ModelBinder<T> binder) {
		this.binder = binder;
		bindRequiredProperties();
	}

	private void bindRequiredProperties() {
		// create bindings for required properties
		for(DataTypeProperty<T, ?> property : binder.getProperties()) {
			if(property.isRequired()) {
				bindings.put(property, new ConstantColumnBinding(null));
			}
		}
	}

	public void setSource(ImportSource source) {
		this.source = source;
		
		// clear calculations based on this source
		this.bindings = Maps.newHashMap();
		this.models = null;
		
		bindRequiredProperties();
	}

	public List<DraftModel<T>> getDraftModels() {
		if(models == null) {
			models = Lists.newArrayListWithCapacity(source.getRows().size());
			for(int i=0;i!=source.getRows().size();++i) {
				models.add(new DraftModel<T>(i));
			}
		}
		return models;
	}
	
	/**
	 * Rebinds all draft models to their columns
	 */
	public List<T> bind() {
		List<T> models = Lists.newArrayList();
		for(DraftModel<T> draftModel : getDraftModels()) {
			T model = binder.newModel();
			for(Map.Entry<DataTypeProperty<T, ?>, ColumnBinding> binding : bindings.entrySet() ) {
				String importedValue = binding.getValue().getValue(draftModel.getRowIndex());
				if(importedValue != null) {
					binding.getKey().tryConvertAndUpdate(model, importedValue);
				}
			}
			models.add(model);
		}	
		return models;
	}
	
	public ImportSource getSource() {
		return source;
	}
	
	public ModelBinder<T> getBinder() {
		return binder;
	}
	
	public Map<DataTypeProperty<T, ?>, ColumnBinding> getColumnBindings() {
		return bindings;
	}
	
	public void setColumnBinding(DataTypeProperty<T, ?> property, ColumnBinding binding) {
		bindings.put(property, binding);
	}

	public void clearColumnBinding(DataTypeProperty<T, ?> property) {
		if(property.isRequired()) {
			bindings.put(property, new ConstantColumnBinding(null));
		} else {
			bindings.remove(property);
		}
	}
	
	/**
	 * Finds the {@code Property} bound to an imported column.
	 * @param columnIndex the index of the column
	 * @return the 
	 */
	public DataTypeProperty<T, ?> propertyForColumn(int columnIndex) {
		for(Map.Entry<DataTypeProperty<T, ?>, ColumnBinding> binding : bindings.entrySet()) {
			if(binding.getValue() instanceof ImportedColumnBinding) {
				ImportedColumnBinding columnBinding = (ImportedColumnBinding) binding.getValue();
				if(columnBinding.getColumnIndex() == columnIndex) {
					return binding.getKey();
				}
			}
		}
		return null;
	}
}
