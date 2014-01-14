package org.activityinfo.client.importer.page;

import java.util.Map;

import org.activityinfo.client.importer.binding.DraftModel;
import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.binding.InstanceMatch;
import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.ont.PropertyPath;

import com.google.common.collect.Maps;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * A second DataGrid that allows the user to the resolve
 * any problems before import
 */
public class ValidationGrid<T> extends ResizeComposite implements UpdateCommandFactory<T> {

	private DataGrid<DraftModel> dataGrid;
	private ImportModel<T> importModel;
	private Map<PropertyPath, PropertyColumn<?>> columns;
	
	public ValidationGrid(ImportModel<T> importModel) {
		this.importModel = importModel;
		this.dataGrid = new BootstrapDataGrid<DraftModel>(100);
		
		syncColumns();
		
		initWidget(dataGrid);
	}

	private void syncColumns() {
		while(dataGrid.getColumnCount() > 0) {
			dataGrid.removeColumn(0);
		}
		columns = Maps.newHashMap();
	
		for(PropertyPath property : importModel.getPropertiesToValidate()) {
			PropertyColumn<?> column = createColumn(property);
			
			columns.put(property, column);
			dataGrid.addColumn(column, property.getLabel());
		}
	}
	

	private PropertyColumn<?> createColumn(PropertyPath path) {
		if(path.getProperty() instanceof DataTypeProperty) {
			switch(path.asDatatypeProperty().getType()) {
			case STRING:
				return new PropertyColumn<String>(path, new EditTextCell());
			}
			throw new IllegalArgumentException(path.asDatatypeProperty().getType().name());
		} else {
			return new PropertyColumn<InstanceMatch>(path, new InstanceMatchCell());
		}
		
	}
	
	public void refreshRows() {
		syncColumns();
		dataGrid.setRowData(importModel.getDraftModels());
	}

	@Override
	public ScheduledCommand setColumnValue(final PropertyPath property, final String value) {
		return new ScheduledCommand() {
			
			@Override
			public void execute() {
				for(DraftModel draftModel : importModel.getDraftModels()) {
					draftModel.setValue(property.getKey(), value);
				}
				dataGrid.redraw();
			}
		};
	}
}
