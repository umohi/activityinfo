package org.activityinfo.ui.full.client.importer.ui.validation;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.BootstrapDataGrid;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.UpdateCommandFactory;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ImportColumn;

import java.util.Map;

/**
 * A second DataGrid that allows the user to the resolve
 * any problems before import
 */
public class ValidationGrid<T> extends ResizeComposite implements UpdateCommandFactory<T> {

    private DataGrid<DraftInstance> dataGrid;
    private ImportModel<T> importModel;
    private Map<FieldPath, ImportColumn<?>> columns;

    public ValidationGrid(ImportModel<T> importModel) {
        this.importModel = importModel;
        this.dataGrid = new BootstrapDataGrid<DraftInstance>(100);

        syncColumns();

        initWidget(dataGrid);
    }

    private void syncColumns() {
        while (dataGrid.getColumnCount() > 0) {
            dataGrid.removeColumn(0);
        }
        columns = Maps.newHashMap();
//
//        for (FieldPath property : importer.getPropertiesToValidate()) {
//            ImportColumn<?> column = createColumn(property);
//
//            columns.put(property, column);
//            dataGrid.addColumn(column, property.getLabel());
//        }
    }


    private ImportColumn<?> createColumn(FieldPath path) {
        throw new UnsupportedOperationException();
    }

    public void refreshRows() {
        syncColumns();
        //dataGrid.setRowData(importer.getDraftModels());
    }

    @Override
    public ScheduledCommand setColumnValue(final FieldPath property, final String value) {
        return new ScheduledCommand() {

            @Override
            public void execute() {
//                for (DraftModel draftModel : importer.getDraftModels()) {
//                    draftModel.setValue(property.getKey(), value);
//                }
                dataGrid.redraw();
            }
        };
    }
}
