package org.activityinfo.core.shared.serialization;

import com.google.common.collect.Maps;
import com.google.gwt.json.client.JSONValue;

import java.util.Map;

/**
 * A serialized key-value object
 */
public class SerObject extends SerValue {
    private final Map<String, SerValue> fields = Maps.newHashMap();

    public void set(String key, SerValue value) {
        fields.put(key, value);
    }

    public void set(String key, String value) {
        fields.put(key, new SerString(value));
    }

    public Iterable<String> keySet() {
        return fields.keySet();
    }

    public SerValue get(String key) {
        return fields.get(key);
    }
}
