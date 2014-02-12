package org.activityinfo.ui.full.client.importer.columns;

import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.ValueStatus;

/**
 * Column for a Field that has not been mapped to a
 * source column and is required.
 */
public class MissingFieldColumn implements DraftColumn {

    private FormField formField;

    public MissingFieldColumn(FormField formField) {
        this.formField = formField;
    }

    @Override
    public LocalizedString getHeader() {
        return formField.getLabel();
    }

    @Override
    public String getValue(DraftInstance instance) {
        return null;
    }

    @Override
    public ValueStatus getStatus(DraftInstance instance) {
        if(formField.isRequired()) {
            return ValueStatus.WARNING;
        } else {
            return ValueStatus.OK;
        }
    }
}
