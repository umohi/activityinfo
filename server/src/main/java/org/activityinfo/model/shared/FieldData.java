package org.activityinfo.model.shared;

public interface FieldData {
	
	/**
	 * @return the cuid of the field for which this object provides data
	 */
	Iri getFieldCuid();
	
	Number getNumericValue();
	
	String getStringValue();
	
	

}
