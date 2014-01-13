package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.ColumnBinding;
import org.activityinfo.client.importer.binding.DraftModel;
import org.activityinfo.client.importer.ont.DataTypeProperty;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.user.cellview.client.Column;

public class PropertyColumn<T> extends Column<DraftModel<T>, String> {

	private final DataTypeProperty<T, ?> property;
	private ColumnBinding binding;

	protected PropertyColumn(DataTypeProperty<T, ?> property, Cell<String> cell) {
		super(cell);
		this.property = property;
	}
	
	public void setBinding(ColumnBinding binding) {
		this.binding = binding;
	}

	@Override
	public String getValue(DraftModel<T> object) {
		String value = getImportedValue(object);
		if(value == null) {
			return "";
 		} else {
 			return value;
 		}
	}

	private String getImportedValue(DraftModel<T> object) {
		String value = binding.getValue(object.getRowIndex());
		return value;
	}

	@Override
	public String getCellStyleNames(Context context, DraftModel<T> object) {
		if(object != null && property.isRequired() && getImportedValue(object) == null) {
			return "danger";
		} 
		return null;
	}
}
