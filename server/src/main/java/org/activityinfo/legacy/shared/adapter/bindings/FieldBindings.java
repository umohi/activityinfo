package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.core.shared.form.FormInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static utility functions for FieldBindings
 */
public class FieldBindings {

    public static Map<String, Object> propertyMap(FormInstance instance, List<FieldBinding<?>> bindings) {
        Map<String, Object> map = new HashMap<>();
        for(FieldBinding<?> binding : bindings) {
            binding.populateChangeMap(instance, map);
        }
        return map;
    }
}
