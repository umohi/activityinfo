package org.activityinfo.model.shared;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * A group of form fields
 */
public interface FormSection {

	/**
	 * 
	 * @return the cuid of this section
	 */
	@NotNull
	Iri getCuid();
	
	/**
	 * 
	 * @return the parent section of this section 
	 * if nested
	 */
	@Nullable
	Iri getParentSectionCuid();
		
	
	/**
	 * 
	 * @return this section's title, generally short and 
	 */
	@NotNull
	@Localizable
	String getTitle();
	
	
}
