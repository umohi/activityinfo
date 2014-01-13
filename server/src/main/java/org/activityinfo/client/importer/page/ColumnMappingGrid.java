package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.data.ImportColumnDescriptor;
import org.activityinfo.client.importer.data.ImportRow;
import org.activityinfo.client.importer.ont.DataTypeProperty;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A DataGrid that shows the original columns in the imported table 
 * and focuses on helping the user select columns as a whole and map them
 * to existing properties.
 */
public class ColumnMappingGrid<T> extends ResizeComposite {

	
	private DataGrid<ImportRow> dataGrid;
	
	private final ImportModel<T> mapping;
	
	public ColumnMappingGrid(ImportModel<T> mapping) {
		
		this.mapping = mapping;
		
		dataGrid = new BootstrapDataGrid<ImportRow>(100);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setSelectionModel(new NullRowSelectionModel());
		dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		dataGrid.addCellPreviewHandler(new Handler<ImportRow>() {

			@Override
			public void onCellPreview(CellPreviewEvent<ImportRow> event) {
				if(BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
					fireEvent(new ColumnSelectionChangedEvent(event.getColumn()));
				}
			}
		});
		
		initWidget(dataGrid);
	}
	
	public void refresh() {
		for(int i=0;i!=dataGrid.getColumnCount();++i) {
			dataGrid.removeColumn(i);
		}
		for(ImportColumnDescriptor column : mapping.getSource().getColumns()) {
			dataGrid.addColumn(new ImportedColumn(column.getIndex()), 
					new ImportColumnHeader(column.getIndex()) );
		}
		dataGrid.setRowData(mapping.getSource().getRows());
	}

	public void refreshMappings() {
		dataGrid.redrawHeaders();
	}
	
	public HandlerRegistration addColumnSelectionChangedHandler(ColumnSelectionChangedEvent.Handler handler) {
		return addHandler(handler, ColumnSelectionChangedEvent.TYPE);
	}
	
	public interface CellTemplates extends SafeHtmlTemplates {
		
		@Template("<span style=\"text-decoration: line-through\">{0}</span>")
		SafeHtml ignoredColumn(String heading);
		
		@Template("{0} » {1}")
		SafeHtml boundColumn(String heading, String mapping);
		
	}
	
	private static final CellTemplates TEMPLATES = GWT.create(CellTemplates.class);
	
	private class ImportColumnHeaderCell extends AbstractCell<Integer> {

		@Override
		public void render(Context context, Integer columnIndex, SafeHtmlBuilder sb) {
			String header = mapping.getSource().getColumnHeader(columnIndex);
			DataTypeProperty<T, ?> binding = mapping.propertyForColumn(columnIndex);
			
			if(binding == null) {
				sb.append(TEMPLATES.ignoredColumn(header));
			} else {
				sb.append(TEMPLATES.boundColumn(header, binding.getLabel()));
			}
		}
	}
	
	private class ImportColumnHeader extends Header<Integer> {
		private int index;

		public ImportColumnHeader(int index) {
			super(new ImportColumnHeaderCell());
			this.index = index;
		}

		@Override
		public Integer getValue() {
			return index;
		}
	}
}
