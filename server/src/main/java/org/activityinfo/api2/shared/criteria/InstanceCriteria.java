package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.Iri;

import java.util.Set;

/**
 * Spike for instance criteria. Defines the criteria
 * for the query as FormInstances that are classes of the
 * given Iri.
 */
public class InstanceCriteria {

    private Set<Iri> classes;

    /**
     *
     * @param classes
     */
    public InstanceCriteria(Set<Iri> classes) {
        this.classes = classes;
    }
}
