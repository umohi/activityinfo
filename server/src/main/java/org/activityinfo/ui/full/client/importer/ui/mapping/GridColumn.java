package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

class GridColumn extends Column<SourceRow, String> {
    private SourceColumn column;

    public GridColumn(SourceColumn column) {
        super(new TextCell());
        this.column = column;
    }

    @Override
    public String getValue(SourceRow row) {
        return row.getColumnValue(column.getIndex());
    }
}
