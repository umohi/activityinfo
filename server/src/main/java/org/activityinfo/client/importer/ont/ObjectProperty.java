package org.activityinfo.client.importer.ont;


public class ObjectProperty<T, C> implements Property {
	
	private String id;
	private String label;
	private String range;
	
	public ObjectProperty(String id, String label, String range) {
		super();
		this.id = id;
		this.label = label;
		this.range = range;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
}
