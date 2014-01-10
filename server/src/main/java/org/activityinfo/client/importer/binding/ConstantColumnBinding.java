package org.activityinfo.client.importer.binding;

public class ConstantColumnBinding implements ColumnBinding {

	private String value;
	
	
	public ConstantColumnBinding(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getValue(int rowIndex) {
		return value;
	}
}
