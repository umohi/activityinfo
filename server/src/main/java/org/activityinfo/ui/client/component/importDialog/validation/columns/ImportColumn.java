package org.activityinfo.ui.client.component.importDialog.validation.columns;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.match.ValueStatus;

public abstract class ImportColumn<C> extends Column<SourceRow, C> {


    public ImportColumn(Cell<C> cell) {
        super(cell);
    }

    public abstract String getHeader();

    /**
     *
     * @return the index of this ImportColumn's {@code SourceColumn}, or -1
     * if it is not mapped to a SourceColumn.
     */
    public abstract int getSourceColumn();

    public abstract FieldPath getFieldPath();


    public boolean isConversionError() {
        return false;
    }

    public boolean isValidationError() {
        return false;
    }

    public ValueStatus getStatus(SourceRow row) {
        return ValueStatus.OK;
    }
}