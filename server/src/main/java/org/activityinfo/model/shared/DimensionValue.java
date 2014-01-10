package org.activityinfo.model.shared;

import javax.validation.constraints.NotNull;

/**
 * A single value within a dimension
 */
public interface DimensionValue {
	
	@NotNull
	Cuid getCuid();
	
	/**
	 * @return the human-readable label 
	 */
	@Localizable
	@NotNull
	String getLabel();
	
	
	Cuid getParent();

}
