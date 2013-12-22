package org.activityinfo.ui.core.client.model;

import java.util.List;

import org.activityinfo.ui.core.client.data.IndexResult;

/**
 * List of DatabaseItems in a given index, along
 * with the date last synced
 */
public interface DatabaseItemList extends IndexResult<DatabaseItem> {

    long getLastSyncedTime();
    
    void setLastSyncedTime(long time);
    
    List<DatabaseItem> getItems();
    
    void setItems(List<DatabaseItem> items);
    
}
