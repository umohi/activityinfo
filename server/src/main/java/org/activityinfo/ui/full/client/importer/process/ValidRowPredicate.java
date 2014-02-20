package org.activityinfo.ui.full.client.importer.process;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Validates a SourceRow against the FormClass's validation rules
 */
public class ValidRowPredicate implements Predicate<SourceRow> {

    private final List<FieldBinding> fieldBindings;

    public ValidRowPredicate(List<FieldBinding> fieldBindings) {
        this.fieldBindings = fieldBindings;
    }

    @Override
    public boolean apply(SourceRow input) {

        for(FieldBinding binding : fieldBindings) {
            Object value = binding.getFieldValue(input);
            if(value == null && binding.getField().isRequired()) {
                return false;
            }
        }
        return true;
    }
}
