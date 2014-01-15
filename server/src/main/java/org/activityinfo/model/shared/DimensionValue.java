package org.activityinfo.model.shared;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * A single value within a dimension
 */
public interface DimensionValue {
	
	@NotNull
	Iri getCuid();
	
	/**
	 * @return the human-readable label 
	 */
	@NotNull
	@Localizable
	String getLabel();
	
	
	Set<Iri> getSuperClasses();
	
	Set<Iri> getSubClassess();

}
