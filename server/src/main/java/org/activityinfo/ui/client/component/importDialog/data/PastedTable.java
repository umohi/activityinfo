package org.activityinfo.ui.client.component.importDialog.data;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.importing.match.ColumnTypeGuesser;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.source.SourceTable;
import org.activityinfo.core.shared.type.converter.ConverterFactory;

import java.util.List;

/**
 * An import source pasted in to a text field by the user.
 */
public class PastedTable implements SourceTable {


    private String text;

    private List<SourceColumn> columns;
    private List<PastedRow> rows;
    private int headerRowCount;


    public PastedTable(String text) {
        this.text = text;
    }


    @Override
    public List<SourceColumn> getColumns() {
        ensureParsed();
        return columns;
    }

    private void ensureParsed() {
        if (rows == null) {
            parseRows();
        }
    }

    private void parseRows() {
        char delimiter = new DelimiterGuesser(text).guess();
        this.rows = new RowParser(text, delimiter).parseRows();
        parseHeaders(rows.get(0));
    }

    private void parseHeaders(PastedRow headerRow) {
        columns = Lists.newArrayList();
        for (int i = 0; i != headerRow.getColumnCount(); ++i) {
            SourceColumn column = new SourceColumn();
            column.setIndex(i);
            column.setHeader(headerRow.getColumnValue(i));
            columns.add(column);
        }
    }

    public void guessColumnsType(ConverterFactory converterFactory) {
        ensureParsed();
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setGuessedType(new ColumnTypeGuesser(columnRowValues(i), converterFactory).guessType());
        }
    }

    private List<String> columnRowValues(int columnIndex) {
        List<String> rowValues = Lists.newArrayList();

        // start from 1 because 0 is header row
        for (int i = 1; i < rows.size(); i++) {
            rowValues.add(rows.get(i).getColumnValue(columnIndex));
        }
        return rowValues;
    }

    @Override
    public List<SourceRow> getRows() {
        ensureParsed();
        headerRowCount = 1;
        return (List)rows.subList(headerRowCount, rows.size());
    }

    public String get(int row, int column) {
        ensureParsed();
        return rows.get(row).getColumnValue(column);
    }

    private String maybeRemoveCarriageReturn(String row) {
        if (row.endsWith("\r")) {
            return row.substring(0, row.length() - 1);
        } else {
            return row;
        }
    }

    @Override
    public String getColumnHeader(Integer columnIndex) {
        return columns.get(columnIndex).getHeader();
    }
}
