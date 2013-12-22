package org.activityinfo.ui.core.client.storage;

/**
 * Key/Value storage for a specific user.
 */
public class UserKeyValueStorage implements KeyValueStorage {

    private int userId;
    private KeyValueStorage storage;
    
    public UserKeyValueStorage(int userId, KeyValueStorage storage) {
        super();
        this.userId = userId;
        this.storage = storage;
    }

    @Override
    public String getItem(String key) {
        return storage.getItem(decorateKey(key));
    }

    @Override
    public void removeItem(String key) {
        storage.removeItem(decorateKey(key));
    }

    @Override
    public void setItem(String key, String data) {
       storage.setItem(decorateKey(key), data);
    }
    
    private String decorateKey(String key) {
        return "u" + userId + "." + key;
    }
}
