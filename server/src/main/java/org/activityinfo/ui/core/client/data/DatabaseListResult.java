package org.activityinfo.ui.core.client.data;

import java.util.Date;
import java.util.List;

import org.activityinfo.ui.core.client.model.DatabaseItem;

public class DatabaseListResult {
    
    /**
     * The date this list was last synced with the server
     */
    private Date lastSynced;
    
    private List<DatabaseItem> items;

    public Date getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(Date lastSynced) {
        this.lastSynced = lastSynced;
    }

    public List<DatabaseItem> getItems() {
        return items;
    }

    public void setItems(List<DatabaseItem> items) {
        this.items = items;
    }
 }
