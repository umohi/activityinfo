package org.activityinfo.core.shared.importing.binding;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.importing.SourceRow;

/**
 * Defines a binding between a field and zero or more columns in the {@code ImportSource}
 */
public interface FieldBinding {

    Cuid getFieldId();

    FormField getField();

    Object getFieldValue(SourceRow row);

    void accept(FieldBindingColumnVisitor visitor);

}
