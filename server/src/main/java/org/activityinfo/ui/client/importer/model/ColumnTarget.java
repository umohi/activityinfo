package org.activityinfo.ui.client.importer.model;

import org.activityinfo.core.shared.form.tree.FieldPath;

/**
 * Defines the target for an imported column
 */
public class ColumnTarget {

    public enum Action {
        MAPPED,
        NEW_FIELD,
        IGNORE
    }

    private Action action;
    private FieldPath fieldPath;

    private ColumnTarget(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public boolean isMapped() {
        return action == Action.MAPPED;
    }

    public boolean isImported() {
        return action != Action.IGNORE;
    }

    public FieldPath getFieldPath() {
        if(fieldPath == null) {
            throw new UnsupportedOperationException("Field is not mapped");
        }
        return fieldPath;
    }

    public static ColumnTarget mapped(FieldPath path) {
        ColumnTarget target = new ColumnTarget(Action.MAPPED);
        target.fieldPath = path;
        return target;
    }

    public static ColumnTarget ignored() {
        return new ColumnTarget(Action.IGNORE);
    }

    public static ColumnTarget newField() {
        return new ColumnTarget(Action.NEW_FIELD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnTarget that = (ColumnTarget) o;

        if (action != that.action) return false;
        if (fieldPath != null ? !fieldPath.equals(that.fieldPath) : that.fieldPath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = action.hashCode();
        result = 31 * result + (fieldPath != null ? fieldPath.hashCode() : 0);
        return result;
    }
}
