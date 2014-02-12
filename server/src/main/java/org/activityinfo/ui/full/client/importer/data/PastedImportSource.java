package org.activityinfo.ui.full.client.importer.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * An import source pasted in to a text field by the user.
 */
public class PastedImportSource implements ImportSource {

    private static final char QUOTE_CHAR = '"';
    private String text;
    //private List<Integer> rowStarts;
    private List<ImportColumnDescriptor> columns;
    private List<SourceRow> rows;

    private String delimeter;

    public PastedImportSource(String text) {
        this.text = text;
    }


    @Override
    public List<ImportColumnDescriptor> getColumns() {
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

        int rowStarts = headerEnds + 1;
        while (true) {
            int rowEnds = text.indexOf('\n', rowStarts);
            if (rowEnds == -1) {
                return;
            }

            rows.add(new PastedImportRow(parseRow(text.substring(rowStarts, rowEnds))));
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
            ImportColumnDescriptor column = new ImportColumnDescriptor();
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

    @Override
    public Set<String> distinctValues(int columnIndex) {
        Set<String> set = Sets.newHashSet();
        for(SourceRow row : getRows()) {
            String value = row.getColumnValue(columnIndex);
            if(value != null) {
                String trimmedValue = value.trim();
                if(trimmedValue.length() > 0) {
                    set.add(trimmedValue.toLowerCase());
                }
            }
        }
        return set;
    }
}
