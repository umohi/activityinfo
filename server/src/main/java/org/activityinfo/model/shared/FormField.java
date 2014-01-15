package org.activityinfo.model.shared;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The smallest logical unit of data entry. A single field can yield
 * multiple dimensions or measures however.
 */
public interface FormField {

	/**
	 * 
	 * @return the form's cuid
	 */
	Iri getCuid();
	
	@Nullable
	FormSection getSectionCuid();
	
	/**
	 * @return this field's label, presented to the user during data entry
	 */
	@NotNull
	@Localizable	
	String getLabel();
	
	/**
	 * @return an extended description of this field, presented to be
	 * presented to the user during data entry
	 */
	@Localizable
	String getDescription();
	
	/**
	 * @return this field's type
	 */
	FormFieldType getType();
	
	/**
	 * 
	 * @return the expression used to calculate this field's value if it is
	 * not provided by the user
	 */
	String getCalculation();
	
	/**
	 * 
	 * @return true if this field is read-only. 
	 */
	boolean isReadOnly();
	
	/**
	 * 
	 * @return true if this field is visible to the user
	 */
	boolean isVisible();
	
	
	/**
	 * 
	 * @return a list of dimensions for which the user is to provide 
	 * responses
	 */
	List<Iri> getDisaggregationDimensions();
	
	/**
	 * 
	 * @return the dimension value on which this field's numeric
	 * value should be loaded
	 */
	Set<DimensionValueRef> getLoadings();
	
	
}
