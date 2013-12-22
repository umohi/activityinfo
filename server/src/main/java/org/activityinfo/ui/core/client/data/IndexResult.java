package org.activityinfo.ui.core.client.data;

import java.util.List;


public interface IndexResult<T> {
    
    long getLastSyncedTime();
    
    List<T> getItems();
}
