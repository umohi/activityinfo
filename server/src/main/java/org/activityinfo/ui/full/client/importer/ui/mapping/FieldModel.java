package org.activityinfo.ui.full.client.importer.ui.mapping;

import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;

/**
 * A "view model" of a {@code FormField} that can be matched against
 */
public class FieldModel {
    private String label;
    private ColumnTarget target;

    public FieldModel(String label, ColumnTarget target) {
        this.label = label;
        this.target = target;
    }

    public FieldModel(String label, FieldPath path) {
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
