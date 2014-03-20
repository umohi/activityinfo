package org.activityinfo.ui.client.component.importDialog.data;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.importing.SourceColumn;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.SourceTable;

import java.util.List;

/**
 * An import source pasted in to a text field by the user.
 */
public class PastedTable implements SourceTable {

    private static final char QUOTE_CHAR = '"';
    private String text;
    //private List<Integer> rowStarts;
    private List<SourceColumn> columns;
    private List<SourceRow> rows;

    private String delimeter;

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

        this.rows = Lists.newArrayList();
        int headerEnds = text.indexOf('\n');
        String headerRow = text.substring(0, headerEnds);
        this.delimeter = guessDelimeter(headerRow);

        String[] headers = parseRow(headerRow);
        parseHeaders(headers);

        int rowIndex = 0;

        int rowStarts = headerEnds + 1;
        while (true) {
            int rowEnds = text.indexOf('\n', rowStarts);
            if (rowEnds == -1) {
                return;
            }

            rows.add(new PastedRow(parseRow(text.substring(rowStarts, rowEnds)), rowIndex++));
            rowStarts = rowEnds + 1;
        }
    }


    private String[] parseRow(String row) {
        row = maybeRemoveCarriageReturn(row);
        boolean usesQuote = row.indexOf(QUOTE_CHAR) != -1;
        if (usesQuote) {
            List<String> cols = Lists.newArrayList();
            boolean quoted = false;
            char delimiterChar = delimeter.charAt(0);
            StringBuilder col = new StringBuilder();

            int charIndex = 0;
            int numChars = row.length();
            while (charIndex < numChars) {
                char c = row.charAt(charIndex);
                if (c == QUOTE_CHAR) {
                    if (charIndex + 1 < numChars && row.charAt(charIndex + 1) == QUOTE_CHAR) {
                        col.append(QUOTE_CHAR);
                        charIndex += 2;
                    } else {
                        quoted = !quoted;
                        charIndex++;
                    }
                } else if (!quoted && c == delimiterChar) {
                    cols.add(col.toString());
                    col.setLength(0);
                    charIndex++;
                } else {
                    col.append(c);
                    charIndex++;
                }
            }

            // final column
            cols.add(col.toString());

            String[] array = new String[cols.size()];
            for(int i=0;i!=array.length;++i) {
                array[i] = cols.get(i);
            }

            return array;

        } else {
            return row.split(delimeter);
        }
    }

    private String guessDelimeter(String headerRow) {
        if (headerRow.contains("\t")) {
            return "\t";
        } else {
            return ",";
        }
    }

    private void parseHeaders(String headers[]) {
        columns = Lists.newArrayList();
        for (int i = 0; i != headers.length; ++i) {
            SourceColumn column = new SourceColumn();
            column.setIndex(i);
            column.setHeader(headers[i]);
            columns.add(column);
        }
    }

    @Override
    public List<SourceRow> getRows() {
        ensureParsed();
        return rows;
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
