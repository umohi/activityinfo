package org.activityinfo.ui.client.component.importDialog.validation;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import org.activityinfo.core.shared.importing.strategy.ColumnAccessor;
import org.activityinfo.core.shared.importing.validation.ValidatedRow;

public class ValidationRowGridColumn extends Column<ValidatedRow, String> {

    private final ColumnAccessor accessor;

    /**
     * Construct a new Column with a given {@link com.google.gwt.cell.client.Cell}.
     *
     * @param accessor
     */
    public ValidationRowGridColumn(ColumnAccessor accessor) {
        super(new TextCell());
        this.accessor = accessor;
    }

    @Override
    public String getValue(ValidatedRow row) {
        return accessor.getValue(row.getSourceRow());
    }
}
