package org.activityinfo.ui.core.client.model;

import java.util.List;


/**
 * Marker interface for List of Models. 
 * 
 * <p>The list is considered a model in of itself as 
 * it also has a last sync date.
 * 
 * @param <T> the type of the list's items.
 */
public interface ModelList<T> extends Model {

    List<T> getItems();
}
