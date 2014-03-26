package org.activityinfo.ui.client.component.importDialog.data;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Parses delimited text files into rows and columns
 */
public class RowParser {

    public static final char QUOTE_CHAR = '"';

    private List<PastedRow> rows = Lists.newArrayList();
    private String text;
    private int length;
    private int currentPos = 0;
    private char delimiter;
    private int rowIndex;
    private int maxRowCount = Integer.MAX_VALUE;

    public RowParser(String text, char delimiter) {
        this.text = text;
        this.length = text.length();
        this.delimiter = delimiter;
    }

    public RowParser withMaxRows(int maxRowCount) {
        this.maxRowCount = maxRowCount;
        return this;
    }


    public List<PastedRow> parseRows() {
        while(!eof() && rows.size() < maxRowCount) {
            readNextLine();
        }
        return rows;
    }

    private void readNextLine() {
        List<Integer> offsets = Lists.newArrayList();
        offsets.add(currentPos);
        while(advanceToNextColumn()) {
            offsets.add(currentPos);
        }
        offsets.add(currentPos);
        PastedRow row = new PastedRow(text, offsets, rowIndex++);
        rows.add(row);
    }

    private boolean advanceToNextColumn() {
        if(text.charAt(currentPos) == QUOTE_CHAR) {
            currentPos++;
            return advanceThroughQuotedColumn();
        }
        char c;
        while(true) {
            if(currentPos == length) {
                c = '\n';
                currentPos++; // advance position as if there had been a trailing newline
            } else {
                c = text.charAt(currentPos++);
            }

            if(c == delimiter) {
                return true; // more to come
            } else if(c == '\n') {
                return false;
            }
        }
    }

    private boolean advanceThroughQuotedColumn() {
        while(true) {
            if(currentPos == length) {
                // unterminated quote, handle gracefully
                // advance two characters for the terminating quote
                // and the missing newline
                currentPos = currentPos + 2;
                return false;
            }
            char c = text.charAt(currentPos++);
            if(c == QUOTE_CHAR) {
                // typically quotes withing the column are escaped by being doubled
                // but more generally, we only consider it the end of the column if it's followed
                // by a column or row terminator
                if(currentPos == length) {
                    return false;
                }
                char nextChar = text.charAt(currentPos++);
                if(nextChar == '\n') {
                    return false;
                } else if(nextChar == delimiter) {
                    return true;
                }
            }
        }
    }


    private boolean eof() {
        return currentPos >= length;
    }
}
