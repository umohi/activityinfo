package org.activityinfo.client.importer.binding;

import java.util.Map;

import org.activityinfo.client.importer.ont.OntClass;
import org.activityinfo.client.importer.ont.OntClassResolver;
import org.activityinfo.client.importer.ont.PropertyPath;
import org.activityinfo.shared.command.Command;

/**
 * Binds an import table to instances of class {@code T}
 */
public interface InstanceImporter extends OntClassResolver {

	OntClass getOntClass();
	
	Map<PropertyPath, Object> getProvidedValues();

	InstanceMatch matchInstance(String ontClass, Map<String, Object> properties);

	Command<?> createCommand(DraftModel draftModel);
	
}
