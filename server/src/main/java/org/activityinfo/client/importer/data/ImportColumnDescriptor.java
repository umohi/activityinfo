package org.activityinfo.client.importer.data;

/**
 * Describes a column in the imported table
 *
 */
public class ImportColumnDescriptor {
	
	private String header;
	private int index;
	
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
