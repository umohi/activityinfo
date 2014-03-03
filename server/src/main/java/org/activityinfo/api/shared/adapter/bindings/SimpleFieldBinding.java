package org.activityinfo.api.shared.adapter.bindings;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.Map;

/**
 * Created by alex on 2/22/14.
 */
public class SimpleFieldBinding implements FieldBinding<EntityDTO> {
    private final Cuid fieldId;
    private final String propertyName;

    public SimpleFieldBinding(Cuid fieldId, String propertyName) {
        this.fieldId = fieldId;
        this.propertyName = propertyName;
    }

    @Override
    public void updateInstanceFromModel(FormInstance instance, EntityDTO model) {
        Object value = model.get(propertyName);
        if (value != null) {
            if (value instanceof LocalDate) {
                final LocalDate localDate = (LocalDate) value;
                instance.set(fieldId, localDate.atMidnightInMyTimezone());
            } else {
                instance.set(fieldId, value);
            }
        }
    }

    @Override
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap) {
        changeMap.put(propertyName, instance.get(fieldId));
    }
}
