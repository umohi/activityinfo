package org.activityinfo.ui.full.client.importer.data;


public class PastedRow implements SourceRow {

    private String[] columns;

    public PastedRow(String[] columns) {
        super();
        this.columns = columns;
    }

    @Override
    public String getColumnValue(int index) {
        if (index < columns.length) {
            return columns[index];
        } else {
            return "";
        }
    }
}
