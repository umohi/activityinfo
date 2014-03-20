package org.activityinfo.core.shared.importing;

import java.util.List;

public interface SourceTable {

    List<SourceColumn> getColumns();

    List<SourceRow> getRows();

    String getColumnHeader(Integer columnIndex);

}
