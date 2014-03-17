package org.activityinfo.ui.client.component.importDialog.validation;

import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.activityinfo.core.client.type.formatter.GwtQuantityFormatterFactory;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.activityinfo.ui.client.component.importDialog.data.SourceRow;
import org.activityinfo.ui.client.component.importDialog.validation.cells.ValidationCellTemplates;
import org.activityinfo.ui.client.component.importDialog.validation.columns.ColumnFactory;
import org.activityinfo.ui.client.component.importDialog.validation.columns.ImportColumn;
import org.activityinfo.ui.client.style.table.DataGridResources;

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
