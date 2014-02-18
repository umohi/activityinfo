package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

public class SourceCellColumn extends Column<SourceRow, String> {
    private int columnIndex;

    public SourceCellColumn(int columnIndex) {
        super(new TextCell());
        this.columnIndex = columnIndex;
    }

    @Override
    public String getValue(SourceRow row) {
        return row.getColumnValue(columnIndex);
    }
}
