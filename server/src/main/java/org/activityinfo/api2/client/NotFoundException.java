package org.activityinfo.api2.client;

import org.activityinfo.api2.shared.Iri;

/**
 * Indicates that the resource was not found on the server
 * or was not visible to the user
 */
public class NotFoundException extends RemoteException {

    private Iri resourceId;

    public NotFoundException() {
    }

    public NotFoundException(Iri resourceId) {
        super("Resource: " + resourceId);
        this.resourceId = resourceId;
    }


    public Iri getResourceId() {
        return resourceId;
    }
}
