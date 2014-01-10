package org.activityinfo.client.importer.page;

import java.util.Map;

import org.activityinfo.client.importer.binding.ColumnBinding;
import org.activityinfo.client.importer.binding.ConstantColumnBinding;
import org.activityinfo.client.importer.binding.DraftModel;
import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.binding.Property;

import com.google.common.collect.Maps;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * A second DataGrid that allows the user to the resolve
 * any problems before import
 */
public class ValidationGrid<T> extends ResizeComposite implements UpdateCommandFactory<T> {

	private DataGrid<DraftModel<T>> dataGrid;
	private ImportModel<T> importModel;
	private Map<Property<T, ?>, PropertyColumn<T>> columns;
	
	public ValidationGrid(ImportModel<T> importModel) {
		this.importModel = importModel;
		this.dataGrid = new DataGrid<DraftModel<T>>();
		
		syncColumns();
		
		initWidget(dataGrid);
	}

	private void syncColumns() {
		while(dataGrid.getColumnCount() > 0) {
			dataGrid.removeColumn(0);
		}
		columns = Maps.newHashMap();
		for(Map.Entry<Property<T, ?>, ColumnBinding> binding : importModel.getColumnBindings().entrySet()) {
			PropertyColumn<T> column = new PropertyColumn<T>(createCell(binding.getKey()));
			column.setBinding(binding.getValue());
			
			columns.put(binding.getKey(), column);
			dataGrid.addColumn(column, binding.getKey().getLabel());
		}
	}
	
	private Cell<String> createCell(Property<T, ?> property) {
		switch(property.getType()) {
		case FREE_TEXT:
			return new EditTextCell();

		case CHOICE:
			return new PopupEditorCell(new ChoiceCellPopup<T>(property, this));
		}
		
		throw new IllegalArgumentException();
	}

	public void refreshRows() {
		syncColumns();
		dataGrid.setRowData(importModel.getDraftModels());
	}

	@Override
	public ScheduledCommand setColumnValue(final Property<T, ?> property, final String value) {
		return new ScheduledCommand() {
			
			@Override
			public void execute() {
				ConstantColumnBinding binding = new ConstantColumnBinding(value);
				importModel.setColumnBinding(property, binding);
				columns.get(property).setBinding(binding);
				dataGrid.redraw();
			}
		};
	}
}
