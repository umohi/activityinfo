package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

/**
 * Defines a binding between a field and zero or more columns in the {@code ImportSource}
 */
public interface FieldBinding {

    Cuid getFieldId();

    FormField getField();

    Object getFieldValue(SourceRow row);

    void accept(FieldBindingColumnVisitor visitor);

}
