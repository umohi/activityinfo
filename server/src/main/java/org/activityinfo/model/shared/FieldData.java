package org.activityinfo.model.shared;

public interface FieldData {
	
	/**
	 * @return the cuid of the field for which this object provides data
	 */
	Cuid getFieldCuid();
	
	Number getNumericValue();
	
	String getStringValue();
	
	

}
