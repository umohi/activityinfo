package org.activityinfo.ui.full.client.importer.process;

import com.google.common.base.Function;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Validates a given row and keeps a tally of valid / invalid rows
 */
public class CountValidRows implements Function<SourceRow, Void> {

    private final List<FieldBinding> bindings;
    private int validCount = 0;
    private int invalidCount = 0;

    public CountValidRows(List<FieldBinding> bindings) {
        this.bindings = bindings;
    }

    @Nullable
    @Override
    public Void apply(SourceRow input) {
        if(validate(input)) {
            validCount++;
        } else {
            invalidCount++;
        }
        return null;
    }

    private boolean validate(SourceRow input) {
        for(FieldBinding binding : bindings) {
            Object value = binding.getFieldValue(input);
            if(value == null && binding.getField().isRequired()) {
                return false;
            }
        }
        return true;
    }

    public int getValidCount() {
        return validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }
}
