package org.activityinfo.ui.client.component.importDialog.data;

import java.util.List;

public interface SourceTable {

    List<SourceColumn> getColumns();

    List<SourceRow> getRows();

    String getColumnHeader(Integer columnIndex);

}
