package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api.shared.command.Command;
import org.activityinfo.ui.full.client.importer.ont.OntClass;
import org.activityinfo.ui.full.client.importer.ont.OntClassResolver;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;

import java.util.Map;

/**
 * Binds an import table to instances of class {@code T}
 */
public interface InstanceImporter extends OntClassResolver {

    OntClass getOntClass();

    Map<PropertyPath, Object> getProvidedValues();

    InstanceMatch matchInstance(String ontClass, Map<String, Object> properties);

    Command<?> createCommand(DraftModel draftModel);

}
