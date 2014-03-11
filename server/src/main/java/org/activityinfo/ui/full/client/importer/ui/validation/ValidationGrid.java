package org.activityinfo.ui.full.client.importer.ui.validation;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.activityinfo.api2.client.formatter.GwtQuantityFormatterFactory;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.ui.Importer;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.ValidationCellTemplates;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ColumnFactory;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ImportColumn;
import org.activityinfo.ui.full.client.style.DataGridResources;

import java.util.Map;

/**
 * A second DataGrid that allows the user to the resolve
 * any problems before import
 */
public class ValidationGrid extends ResizeComposite {

    private Importer importer;
    private ColumnFactory columnFactory;

    private DataGrid<SourceRow> dataGrid;
    private Map<FieldPath, ImportColumn> columns;

    public ValidationGrid(Importer importer) {
        this.importer = importer;
        this.columnFactory = new ColumnFactory(
                new GwtQuantityFormatterFactory(),
                ValidationCellTemplates.INSTANCE,
                importer.getFormTree());

        this.dataGrid = new DataGrid<>(100, DataGridResources.INSTANCE);

        syncColumns();

        initWidget(dataGrid);
    }

    private void syncColumns() {
        while (dataGrid.getColumnCount() > 0) {
            dataGrid.removeColumn(0);
        }
        for(ImportColumn<?> column : columnFactory.create(importer.getBindings())) {
            dataGrid.addColumn(column, new TextHeader(column.getHeader()));
        }
    }

    public void refreshRows() {
        syncColumns();
        dataGrid.setRowData(importer.getRows());
    }
}
