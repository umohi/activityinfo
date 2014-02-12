package org.activityinfo.ui.full.client.importer.data;

import java.util.List;
import java.util.Set;

public interface SourceTable {

    List<SourceColumn> getColumns();

    List<SourceRow> getRows();

    String getColumnHeader(Integer columnIndex);

}
