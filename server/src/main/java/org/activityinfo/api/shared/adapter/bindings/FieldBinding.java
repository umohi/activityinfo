package org.activityinfo.api.shared.adapter.bindings;

import org.activityinfo.api.shared.model.DTO;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.Map;

/**
 * Two-way bridge between FormInstances and Legacy objects
 */
public interface FieldBinding<T extends DTO> {


    /**
     * Updates a FormInstance from a Legacy Model
     * @param instance FormInstance
     * @param model a Legacy Model
     */
    public void updateInstanceFromModel(FormInstance instance, T model);

    /**
     * Updates the toChangePropertyMap
     * @param instance
     * @param changeMap a name -> property value map used to update the model
     */
    public void populateChangeMap(FormInstance instance, Map<String, Object> changeMap);


}
