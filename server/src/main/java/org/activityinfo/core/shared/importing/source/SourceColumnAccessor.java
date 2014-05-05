package org.activityinfo.core.shared.importing.source;

import com.google.common.base.Strings;
import org.activityinfo.core.shared.importing.strategy.ColumnAccessor;

public class SourceColumnAccessor implements ColumnAccessor {

    private final SourceColumn column;
    private final int columnIndex;

    public SourceColumnAccessor(SourceColumn column) {
        this.column = column;
        this.columnIndex = this.column.getIndex();
    }

    @Override
    public String getHeading() {
        return column.getHeader();
    }

    @Override
    public String getValue(SourceRow row) {
        return row.getColumnValue(columnIndex);
    }

    @Override
    public boolean isMissing(SourceRow row) {
        return Strings.isNullOrEmpty(row.getColumnValue(columnIndex));
    }

    @Override
    public String toString() {
        return column.getHeader();
    }
}
