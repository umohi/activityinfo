package org.activityinfo.client.importer.binding;

import java.util.List;

/**
 * Binds an import table to a model of class {@code T}
 */
public interface Binder<T> {

	/**
	 * 
	 * @return a new model of class {@code T}
	 */
	T createNew();
	
	
	/**
	 * 
	 * @return the properties of the model {@code T}
	 */
	List<Property<T, ?>> getProperties();

}
