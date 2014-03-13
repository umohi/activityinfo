package org.activityinfo.api.shared.adapter.bindings;

import org.activityinfo.api.shared.model.EntityDTO;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.fp.shared.BiFunction;

/**
 * Creates a FormInstance from a ModelBinding and a legacy Model
 */
public class InstanceConstructor<T extends EntityDTO>
        extends BiFunction<ModelBinding<T>, T, FormInstance> {


    @Override
    public FormInstance apply(ModelBinding<T> binding, T t) {
        return binding.newInstance(t);
    }
}
