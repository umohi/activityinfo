package org.activityinfo.ui.core.client.storage;

import com.google.gwt.storage.client.Storage;

public class LocalKeyValueStorage implements KeyValueStorage {

    private final Storage storage;

    public LocalKeyValueStorage(Storage storage) {
        super();
        this.storage = storage;
    }


    @Override
    public String getItem(String key) {
        return storage.getItem(key);
    }

    @Override
    public void removeItem(String key) {
        storage.removeItem(key);
    }

    @Override
    public void setItem(String key, String data) {
        storage.setItem(key, data);
    }

}
