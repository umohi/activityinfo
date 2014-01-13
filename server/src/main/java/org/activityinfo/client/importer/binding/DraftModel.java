package org.activityinfo.client.importer.binding;



public class DraftModel<T> {

	private int rowIndex;
	
	public DraftModel(int rowIndex) {
		super();
		this.rowIndex = rowIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
}
