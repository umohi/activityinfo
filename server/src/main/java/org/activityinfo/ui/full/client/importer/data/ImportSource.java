package org.activityinfo.ui.full.client.importer.data;

import java.util.List;
import java.util.Set;

public interface ImportSource {

    List<ImportColumnDescriptor> getColumns();

    List<SourceRow> getRows();

    String getColumnHeader(Integer columnIndex);

    Set<String> distinctValues(int columnIndex);
}
