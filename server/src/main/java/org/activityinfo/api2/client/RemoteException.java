package org.activityinfo.api2.client;

/**
 * A collection of "expected problems" that the UI should gracefully handle.
 */
public class RemoteException extends RuntimeException {

    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }
}
