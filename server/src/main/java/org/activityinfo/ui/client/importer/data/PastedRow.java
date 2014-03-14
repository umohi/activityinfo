package org.activityinfo.ui.client.importer.data;


public class PastedRow implements SourceRow {

    private String[] columns;
    private int rowIndex;

    public PastedRow(String[] columns, int rowIndex) {
        super();
        this.columns = columns;
        this.rowIndex = rowIndex;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public String getColumnValue(int columnIndex) {
        if (columnIndex < columns.length) {
            return columns[columnIndex];
        } else {
            return "";
        }
    }
}
