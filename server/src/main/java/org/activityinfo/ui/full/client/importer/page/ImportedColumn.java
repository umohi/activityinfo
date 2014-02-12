package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

public class ImportedColumn extends Column<SourceRow, String> {
    private int columnIndex;

    public ImportedColumn(int columnIndex) {
        super(new TextCell());
        this.columnIndex = columnIndex;
    }

    @Override
    public String getValue(SourceRow row) {
        return row.getColumnValue(columnIndex);
    }
}
