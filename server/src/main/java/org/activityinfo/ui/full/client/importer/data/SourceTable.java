package org.activityinfo.ui.full.client.importer.data;

import java.util.List;

public interface SourceTable {

    List<SourceColumn> getColumns();

    List<SourceRow> getRows();

    String getColumnHeader(Integer columnIndex);

}
