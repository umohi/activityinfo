package org.activityinfo.ui.core.client.storage;

import org.activityinfo.ui.core.client.AuthenticationController;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gwt.storage.client.Storage;

/**
 * The client side cache.
 * 
 * The cache provides different types and areas of storage to 
 * consumers, depending on the login status, the client's capabilities,
 * and the user's privacy settings for this client.
 */
public class Cache {
    
    private static final String GLOBAL_PREFIX = "g";
    
    private KeyValueStore storage;

    private AuthenticationController authController;

    public Cache(AuthenticationController authController) {
        this.authController = authController;
        
        if(Storage.isLocalStorageSupported()) {
            storage = new LocalKeyValueStore(Storage.getLocalStorageIfSupported());
        } else {
            storage = new HashMapKeyValueStorage();
        }
    }
    
    /**
     * 
     * @return a KeyValueStorage common to all users. <strong>Most not</strong> be used
     * for storage of sensitive or non-public data.
     */
    public KeyValueStore getGlobalKeyValueStorage() {
        return new PrefixedKeyValueStorage(storage, Suppliers.ofInstance(GLOBAL_PREFIX));
    }
    
    /**
     * 
     * @return a KeyValueStore for the currently authenticated user. 
     */
    public KeyValueStore getUserStore() {
        return new PrefixedKeyValueStorage(storage, new Supplier<String>() {
            
            @Override
            public String get() {
                if(authController.isLoggedIn()) {
                    return "u" + authController.getUserId();
                } else {
                    return "anon";
                }
            }
        });
    }
}
