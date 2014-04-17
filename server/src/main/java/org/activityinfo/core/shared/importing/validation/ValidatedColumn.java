package org.activityinfo.core.shared.importing.validation;

import org.activityinfo.core.shared.importing.strategy.ColumnAccessor;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;

/**
 * Results of the validation process
 */
public class ValidatedColumn {

    private final ImportTarget target;
    private final ColumnAccessor column;

    public ValidatedColumn(ImportTarget target, ColumnAccessor column) {
        this.column = column;
        this.target = target;
    }

    public ImportTarget getTarget() {
        return target;
    }

    public ColumnAccessor getAccessor() {
        return column;
    }
}
