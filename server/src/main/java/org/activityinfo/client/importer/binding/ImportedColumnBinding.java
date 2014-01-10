package org.activityinfo.client.importer.binding;

import org.activityinfo.client.importer.data.ImportSource;

public class ImportedColumnBinding implements ColumnBinding {
	
	private ImportSource source;
	private int columnIndex;

	public ImportedColumnBinding(ImportSource source, int columnIndex) {
		this.source = source;
		this.columnIndex = columnIndex;
	}
	
	@Override
	public String getValue(int rowIndex) {
		return source.getRows().get(rowIndex).getColumnValue(columnIndex);
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
}
