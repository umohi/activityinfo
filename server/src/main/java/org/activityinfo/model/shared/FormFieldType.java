package org.activityinfo.model.shared;

/**
 * The type of field, which influences how input is presented
 * the user, how it is validated, and what default measures 
 * are available.
 */
public enum FormFieldType {

	/**
	 * Numeric quantity, expressed in certain units
	 */
	QUANTITY,
	
	/**
	 * A longish block of text
	 */
	NARRATIVE,
	
	/**
	 * Short free text field
	 */
	FREE_TEXT,
	
	/**
	 * A Gregorian calendar date, with no time zone attached
	 */
	LOCAL_DATE,
	
	/**
	 * A geographic point, expressed as latitude / longitude
	 */
	GEOGRAPHIC_POINT,
	
	/**
	 * choice of values
	 */
	CHOICE
}
