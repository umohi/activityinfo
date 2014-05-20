package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.shared.BiFunction;
import org.activityinfo.legacy.shared.model.EntityDTO;

/**
 * Creates a FormInstance from a ModelBinding and a legacy Model
 */
public class InstanceConstructor<T extends EntityDTO> extends BiFunction<ModelBinding<T>, T, FormInstance> {


    @Override
    public FormInstance apply(ModelBinding<T> binding, T t) {
        return binding.newInstance(t);
    }
}
