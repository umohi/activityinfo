package org.activityinfo.model.shared;

/**
 * 
 * The basic unit of data collection and storage. A form is similar to a {@link Table},
 * but is richer in that it contains instructions to the data collector, validation rules,
 * and potentially relationships with other Forms.
 * 
 *
 */
public interface Form {

	Iri getCuid();

	@Localizable
	String getName();

	
	
}
