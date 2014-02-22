package org.activityinfo.ui.full.client.importer.model;

import org.activityinfo.api2.shared.form.tree.FieldPath;

import javax.annotation.Nonnull;

/**
 * Imports the column to an existing Field
 */
public class ImportExistingAction implements ColumnAction {

    private String label;
    private final FieldPath fieldPath;

    public ImportExistingAction(@Nonnull String label, @Nonnull FieldPath fieldPath) {
        this.label = label;
        this.fieldPath = fieldPath;
    }

    @Nonnull
    public FieldPath getFieldPath() {
        return fieldPath;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isSingleColumn() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportExistingAction that = (ImportExistingAction) o;

        return fieldPath.equals(that.fieldPath);

    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + fieldPath.hashCode();
        return result;
    }
}
