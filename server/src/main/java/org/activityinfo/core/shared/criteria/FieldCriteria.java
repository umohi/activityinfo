package org.activityinfo.core.shared.criteria;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Accepts an instance if the given field value matches
 * exactly.
 */
public class FieldCriteria implements Criteria {

    private Cuid fieldId;
    private Object value;

    public FieldCriteria(@Nonnull Cuid fieldId, @Nonnull Object value) {
        this.fieldId = fieldId;
        this.value = value;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitFieldCriteria(this);
    }

    @Override
    public boolean apply(@Nullable FormInstance input) {
        return Objects.equals(input.get(fieldId), value);
    }

    public Cuid getFieldId() {
        return fieldId;
    }

    public Object getValue() {
        return value;
    }
}
