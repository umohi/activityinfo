package org.activityinfo.model.client;


/**
 * Container for an object of class {@code T} potentially
 * located on a server.
 *
 * Remotes can be handed to UI containers which provide a uniform
 * interface for failed fetches, retrying, etc.
 *
 */
public interface Remote<T> {

    Promise<T> fetch();

}
