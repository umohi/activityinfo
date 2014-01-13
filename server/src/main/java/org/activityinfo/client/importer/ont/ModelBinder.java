package org.activityinfo.client.importer.ont;

import java.util.List;

import org.activityinfo.shared.command.Command;

/**
 * Binds an import table to a model of class {@code T}
 */
public interface ModelBinder<T> {

	/**
	 * 
	 * @return a new model of class {@code T}
	 */
	T newModel();
	
	/**
	 * 
	 * @return a Command that will create the given model
	 */
	Command<?> createCommand(T model);
	
	/**
	 * 
	 * @return the properties of the model {@code T}
	 */
	List<Property> getProperties();

}
