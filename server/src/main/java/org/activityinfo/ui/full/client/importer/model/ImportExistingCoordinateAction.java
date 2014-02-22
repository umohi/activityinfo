package org.activityinfo.ui.full.client.importer.model;

import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.model.CoordinateAxis;
import org.activityinfo.ui.full.client.i18n.I18N;

import javax.annotation.Nonnull;

/**
 * Imports a column to an existing Geometry Point column
 */
public class ImportExistingCoordinateAction extends ImportExistingAction {

    private final CoordinateAxis axis;

    public ImportExistingCoordinateAction(FieldPath fieldPath, CoordinateAxis axis) {
        super(label(axis), fieldPath);
        this.axis = axis;
    }

    @Nonnull
    @Override
    public String getLabel() {
        return label(axis);
    }

    private static String label(CoordinateAxis axis) {
        switch(axis) {
            case LATITUDE:
                return I18N.CONSTANTS.latitude();
            case LONGITUDE:
                return I18N.CONSTANTS.longitude();
        }
        throw new IllegalStateException("" + axis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ImportExistingCoordinateAction that = (ImportExistingCoordinateAction) o;

        if (axis != that.axis) return false;
        if (!getFieldPath().equals(that.getFieldPath())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + axis.hashCode();
        return result;
    }
}



