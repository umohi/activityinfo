package org.activityinfo.model.shared;

/**
 * Collision-resistent universal ID. Used to generate ids for objects
 * in a distributed manner
 *
 */
public interface Iri {
	
	/**
	 * 
	 * @return an encoded string representation of the CUID
	 */
	String asString();

}
