package org.activityinfo.ui.core.client.storage;

import com.google.common.base.Supplier;

/**
 * Key/Value storage for a specific user.
 */
public class PrefixedKeyValueStorage implements KeyValueStore {

    private KeyValueStore storage;
    private Supplier<String> prefixSupplier;
    
    public PrefixedKeyValueStorage(KeyValueStore storage, Supplier<String> prefixSupplier) {
        super();
        this.storage = storage;
        this.prefixSupplier = prefixSupplier;
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
        return prefixSupplier.get() + "." + key;
    }
}
