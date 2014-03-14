package org.activityinfo.ui.client.importer.data;

/**
 * Generic interface to a row of an imported table
 */
public interface SourceRow {


    int getRowIndex();

    String getColumnValue(int columnIndex);

}
