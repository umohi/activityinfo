package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.ColumnBinding;
import org.activityinfo.client.importer.binding.DraftModel;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

public class PropertyColumn<T> extends Column<DraftModel<T>, String> {

	private ColumnBinding binding;

	protected PropertyColumn(Cell<String> cell) {
		super(cell);
	}
	
	public void setBinding(ColumnBinding binding) {
		this.binding = binding;
	}

	@Override
	public String getValue(DraftModel<T> object) {
		return binding.getValue(object.getRowIndex());
	}
}
