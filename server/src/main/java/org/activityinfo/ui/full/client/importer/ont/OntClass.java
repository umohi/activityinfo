package org.activityinfo.ui.full.client.importer.ont;

import java.util.List;

/**
 * Describes an "ontological" class. We are borrowing the concepts of class and
 * instances from the RDFS and OWL standards.
 */
public abstract class OntClass {

    /**
     * @return
     */
    public abstract List<Property> getProperties();

}
