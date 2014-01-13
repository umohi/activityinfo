package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.binding.ImportedColumnBinding;
import org.activityinfo.client.importer.ont.DataTypeProperty;
import org.activityinfo.client.importer.page.ColumnSelectionChangedEvent.Handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page that allows the user to match columns in an imported table to 
 * an existing structure.
 */
public class ColumnMappingPage<T> extends ResizeComposite {

	public static int nextUniqueGroupNum = 1;
	
	private static ColumnMatchingPanelUiBinder uiBinder = GWT
			.create(ColumnMatchingPanelUiBinder.class);

	interface ColumnMatchingPanelUiBinder extends
			UiBinder<Widget, ColumnMappingPage<?>> {
	}
	
	interface Style extends CssResource {
		String selectedColumn();
	}
	
	@UiField Style style;
	
	@UiField(provided=true) ColumnMappingGrid<T> dataGrid;
	
	@UiField HeadingElement columnChooserHeader;
	@UiField(provided=true) PropertyChooser<T> propertyChooser;
	
	private final ImportModel<T> importModel;

	private int selectedColumnIndex;	
	
	public ColumnMappingPage(ImportModel<T> importModel) {
		this.importModel = importModel;
		
		dataGrid = new ColumnMappingGrid<T>(importModel);
		propertyChooser = new PropertyChooser<T>(importModel.getBinder().getProperties());
		
		initWidget(uiBinder.createAndBindUi(this));		
		
		dataGrid.addColumnSelectionChangedHandler(new Handler() {
			
			@Override
			public void onColumnSelectionChanged(ColumnSelectionChangedEvent e) {
				onColumnChanged(e);
			}
		});
		
		propertyChooser.addValueChangeHandler(new ValueChangeHandler<DataTypeProperty<T, ?>>() {

			@Override
			public void onValueChange(ValueChangeEvent<DataTypeProperty<T, ?>> event) {
				updateColumnMapping(event.getValue());
			}
		});
	}
	
	/**
	 * 
	 * @param importSource
	 */
	public void refresh() {
		dataGrid.refresh();
	}

	private void onColumnChanged(ColumnSelectionChangedEvent e) {
		selectedColumnIndex = e.getSelectedColumnIndex();
		propertyChooser.setValue(importModel.propertyForColumn(selectedColumnIndex), false);
		columnChooserHeader.setInnerText(importModel.getSource().getColumnHeader(selectedColumnIndex));
	}

	private void updateColumnMapping(DataTypeProperty<T, ?> property) {
		
		if(property == null) {
			DataTypeProperty<T, ?> oldProperty = importModel.propertyForColumn(selectedColumnIndex);
			if(oldProperty != null) {
				importModel.clearColumnBinding(oldProperty);
			}
		} else {
			importModel.setColumnBinding(property, 
					new ImportedColumnBinding(importModel.getSource(), selectedColumnIndex));
		}
		
		dataGrid.refreshMappings();
	}
}