package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.data.ImportRow;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

public class ImportedColumn extends Column<ImportRow, String> {
	private int columnIndex;

	public ImportedColumn(int columnIndex) {
		super(new TextCell());
		this.columnIndex = columnIndex;
	}

	@Override
	public String getValue(ImportRow row) {
		return row.getColumnValue(columnIndex);
	}
}
