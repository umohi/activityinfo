package org.activityinfo.client.importer.binding;


public class DraftModel<T> {

	private T model;
	private int rowIndex;
	
	public DraftModel(T model, int rowIndex) {
		super();
		this.model = model;
		this.rowIndex = rowIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public T getModel() {
		return model;
	}
}
