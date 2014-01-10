package org.activityinfo.client.importer.binding;

/**
 * Binds a column's value to a model
 */
public interface PropertyBinder<T, C> {

	/**
	 * Tries to update the model's property
	 * 
	 * @param model the model to update
	 * @param value the value to be imported
	 * @return true if the value was valid
	 */
	boolean update(T model, C value);
	
}
