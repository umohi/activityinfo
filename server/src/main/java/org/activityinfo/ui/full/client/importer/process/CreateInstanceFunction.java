package org.activityinfo.ui.full.client.importer.process;

import com.google.common.base.Function;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.local.command.handler.KeyGenerator;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Creates an instance from a SourceRow and a list of FieldBindings
 */
public class CreateInstanceFunction implements Function<SourceRow, FormInstance> {

    private FormClass formClass;
    private List<FieldBinding> bindings;
    private Function<SourceRow, Cuid> instanceIdentityFunction;

    public CreateInstanceFunction(FormClass formClass, List<FieldBinding> bindings,
                                  Function<SourceRow, Cuid> instanceIdentityFunction) {
        this.formClass = formClass;
        this.bindings = bindings;
        this.instanceIdentityFunction = instanceIdentityFunction;
    }

    @Override
    public FormInstance apply(SourceRow input) {

        Cuid classId = formClass.getId();
        Cuid instanceId = instanceIdentityFunction.apply(input);

        FormInstance instance = new FormInstance(instanceId, classId);
        for(FieldBinding binding : bindings) {
            Object value = binding.getFieldValue(input);
            if(value == null && binding.getField().isRequired()) {
                // validation failed
                return null;
            }
            instance.set(binding.getFieldId(), value);
        }
        return instance;
    }
}
