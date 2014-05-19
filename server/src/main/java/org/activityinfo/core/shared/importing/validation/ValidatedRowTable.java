package org.activityinfo.core.shared.importing.validation;

import org.activityinfo.core.shared.importing.strategy.FieldImporterColumn;

import java.util.List;

/**
 * Created by alex on 4/4/14.
 */
public class ValidatedRowTable {

    private List<FieldImporterColumn> columns;
    private List<ValidatedRow> rows;

    public ValidatedRowTable(List<FieldImporterColumn> columns, List<ValidatedRow> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<FieldImporterColumn> getColumns() {
        return columns;
    }

    public List<ValidatedRow> getRows() {
        return rows;
    }
}
