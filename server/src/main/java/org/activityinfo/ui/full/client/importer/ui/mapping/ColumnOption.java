package org.activityinfo.ui.full.client.importer.ui.mapping;

import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;

/**
 * Represents a choice a user can make for a given field
 */
public class ColumnOption {
    private String label;
    private ColumnTarget target;

    public ColumnOption(String label, ColumnTarget target) {
        this.label = label;
        this.target = target;
    }

    public ColumnOption(String label, FieldPath path) {
        this.label = label;
        this.target = ColumnTarget.mapped(path);
    }

    public String getLabel() {
        return label;
    }

    public ColumnTarget getTarget() {
        return target;
    }
}
