package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.collect.Maps;

import java.util.Map;


public class DraftModel {

    private int rowIndex;
    private Map<String, Object> propertyValues = Maps.newHashMap();

    public DraftModel(int rowIndex) {
        super();
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public Object getValue(String propertyKey) {
        return propertyValues.get(propertyKey);
    }

    public void setValue(String propertyKey, Object value) {
        propertyValues.put(propertyKey, value);
    }

    public Integer getInstanceId(String propertyKey) {
        InstanceMatch match = (InstanceMatch) propertyValues.get(propertyKey);
        if (match != null) {
            return Integer.parseInt(match.getInstanceId());
        }
        return null;
    }

    public Map<String, ?> asLegacyPropertyMap() {
        Map<String, Object> map = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
            if (!entry.getKey().contains(".")) {
                if (entry.getValue() instanceof InstanceMatch) {
                    InstanceMatch match = (InstanceMatch) entry.getValue();
                    int id = Integer.parseInt(match.getInstanceId());
                    map.put(entry.getKey() + "Id", id);
                } else {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return map;
    }

    @Override
    public String toString() {
        return propertyValues.toString();
    }
}
