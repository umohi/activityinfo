package org.activityinfo.ui.full.client.importer.data;

import java.util.List;

public interface ImportSource {

    List<ImportColumnDescriptor> getColumns();

    List<ImportRow> getRows();

    String getColumnHeader(Integer columnIndex);
}
