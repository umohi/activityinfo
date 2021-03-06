package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.activityinfo.core.shared.importing.model.ColumnAction;
import org.activityinfo.core.shared.importing.model.IgnoreAction;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.ui.client.style.table.DataGridResources;

import java.util.List;

/**
 * A DataGrid that shows the original columns in the imported table
 * and focuses on helping the user select columns as a whole and map them
 * to existing properties.
 */
public class ColumnMappingGrid extends DataGrid<SourceRow> {

    public static final int SOURCE_COLUMN_HEADER_ROW = 0;
    public static final int MAPPING_HEADER_ROW = 1;

    private final ImportModel model;

    private SingleSelectionModel<SourceColumn> columnSelectionModel;
    private List<SourceColumn> sourceColumns;

    private final GridHeaderCell headerCell;

    private int lastSelectedColumn = -1;

    public ColumnMappingGrid(ImportModel model,
                             SingleSelectionModel<SourceColumn> columnSelectionModel) {


        super(50, DataGridResources.INSTANCE);
        
        ColumnMappingStyles.INSTANCE.ensureInjected();
        DataGridResources.INSTANCE.dataGridStyle().ensureInjected();

        this.model = model;
        this.columnSelectionModel = columnSelectionModel;

        headerCell = new GridHeaderCell(model);

        this.addStyleName(ColumnMappingStyles.INSTANCE.grid());
        this.setWidth("100%");
        this.setHeight("100%");
        this.setSelectionModel(new NullRowSelectionModel());
        this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
        this.setHeaderBuilder(new GridHeaderBuilder(this));
        this.setSkipRowHoverCheck(true);
        this.addCellPreviewHandler(new Handler<SourceRow>() {

            @Override
            public void onCellPreview(CellPreviewEvent<SourceRow> event) {
                if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
                    SourceColumn sourceColumn = sourceColumns.get(event.getColumn());
                    ColumnMappingGrid.this.columnSelectionModel.setSelected(sourceColumn, true);
                }
            }
        });

        this.columnSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        onColumnSelectionChanged();
                    }
                });
            }
        });

    }

    private void onColumnSelectionChanged() {

        // ensure the column is scrolled into view
        int newColumnIndex = columnSelectionModel.getSelectedObject().getIndex();
        scrollColumnIntoView(newColumnIndex);

        // clear the selection styles from the old column
        if(lastSelectedColumn != -1) {
            removeHeaderStyleName(lastSelectedColumn, ColumnMappingStyles.INSTANCE.selected());
            this.removeColumnStyleName(lastSelectedColumn, ColumnMappingStyles.INSTANCE.selected());
        }

        // add the bg to the new selection
        this.getHeader(newColumnIndex).setHeaderStyleNames(ColumnMappingStyles.INSTANCE.selected());
        this.addColumnStyleName(newColumnIndex, ColumnMappingStyles.INSTANCE.selected());

        lastSelectedColumn = newColumnIndex;
    }

    /**
     * Updates the column styles to match the column's current binding
     */
    public void refreshColumnStyles(int columnIndex) {
        // update the column styles
        final SourceColumn sourceColumn = model.getSourceColumn(columnIndex);
        ColumnAction binding = model.getColumnAction(sourceColumn);

        toggleColumnStyle(columnIndex, ColumnMappingStyles.INSTANCE.stateIgnored(), binding != null &&
                binding == IgnoreAction.INSTANCE);
        toggleColumnStyle(columnIndex, ColumnMappingStyles.INSTANCE.stateBound(), binding != null &&
                binding != IgnoreAction.INSTANCE);

        toggleColumnStyle(columnIndex, ColumnMappingStyles.INSTANCE.stateUnset(), binding == null);

        // update the mapping description
        Cell.Context context = new Cell.Context(MAPPING_HEADER_ROW, columnIndex, null);
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        headerCell.render(context, sourceColumn, html);

        getTableHead(MAPPING_HEADER_ROW, columnIndex).setInnerSafeHtml(html.toSafeHtml());
    }

    private void scrollColumnIntoView(int selectedColumnIndex) {
        Element td = this.getRowElement(0).getChild(selectedColumnIndex).cast();
        td.scrollIntoView();
    }

    private void addHeaderStyleName(int columnIndex, String className) {
        getTableHead(SOURCE_COLUMN_HEADER_ROW, columnIndex).addClassName(className);
        getTableHead(MAPPING_HEADER_ROW, columnIndex).addClassName(className);
    }

    private void removeHeaderStyleName(int columnIndex, String className) {
        getTableHead(SOURCE_COLUMN_HEADER_ROW, columnIndex).removeClassName(className);
        getTableHead(MAPPING_HEADER_ROW, columnIndex).removeClassName(className);
    }

    private Element getTableHead(int rowIndex, int columnIndex) {
        final TableRowElement row = getTableHeadElement().getRows().getItem(rowIndex);
        return row.getCells().getItem(columnIndex);
    }

    private void toggleColumnStyle(int index, String className, boolean enabled) {
        if(enabled) {
            this.addColumnStyleName(index, className);
            addHeaderStyleName(index, className);

        } else {
            this.removeColumnStyleName(index, className);
            removeHeaderStyleName(index, className);
        }
    }

    public void refresh() {
        while(this.getColumnCount() > 0) {
            this.removeColumn(0);
        }
        sourceColumns = model.getSource().getColumns();
        for (SourceColumn sourceColumn : sourceColumns) {
            GridColumn gridColumn = new GridColumn(sourceColumn);
            GridHeader gridHeader = new GridHeader(sourceColumn, headerCell, columnSelectionModel);
            this.addColumn(gridColumn, gridHeader);
            this.setColumnWidth(gridColumn, 10, com.google.gwt.dom.client.Style.Unit.EM);
        }
        this.redrawHeaders();
        this.setRowData(model.getSource().getRows());
    }
}
