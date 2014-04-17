package org.activityinfo.core.shared.importing.validation;

import java.util.List;

/**
 * Created by alex on 4/4/14.
 */
public class ValidatedTable {

    private List<ValidatedColumn> columns;
    private List<ValidatedRow> rows;

    public ValidatedTable(List<ValidatedColumn> columns, List<ValidatedRow> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<ValidatedColumn> getColumns() {
        return columns;
    }

    public List<ValidatedRow> getRows() {
        return rows;
    }
}
