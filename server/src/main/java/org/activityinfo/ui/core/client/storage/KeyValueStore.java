package org.activityinfo.ui.core.client.storage;

/**
 * An abstraction of the synchronous LocalStorage key value storage 
 * from HTML5. Can be decorated to carve out subdomains for individual
 * users using prefixes.
 */
public interface KeyValueStore {

    /**
     * Returns the item in the Storage associated with the specified key.
     *
     * @param key the key to a value in the Storage
     * @return the value associated with the given key
     */
    String getItem(String key);

    /**
     * Removes the item in the Storage associated with the specified key.
     *
     * @param key the key to a value in the Storage
     */
    void removeItem(String key);

    /**
     * Sets the value in the Storage associated with the specified key to the
     * specified data.
     *
     * Note: The empty string may not be used as a key.
     *
     * @param key the key to a value in the Storage
     * @param data the value associated with the key
     */
    void setItem(String key, String data);
}
