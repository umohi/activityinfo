package org.activityinfo.ui.core.client.storage;

import java.util.Map;

import com.google.common.collect.Maps;

public class HashMapKeyValueStorage implements KeyValueStorage {

    private Map<String, String> map = Maps.newHashMap();
    
    @Override
    public String getItem(String key) {
        return map.get(key);
    }

    @Override
    public void removeItem(String key) {
        map.remove(key);
    }

    @Override
    public void setItem(String key, String data) {
        map.put(key, data);
    }

}
