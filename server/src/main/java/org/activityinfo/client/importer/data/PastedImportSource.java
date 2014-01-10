package org.activityinfo.client.importer.data;

import java.util.List;

import com.google.common.collect.Lists;

/** 
 * An import source pasted in to a text field by the user.
 *
 */
public class PastedImportSource implements ImportSource {

	private String text;
	//private List<Integer> rowStarts;
	private List<ImportColumnDescriptor> columns;
	private List<ImportRow> rows;
	
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
		if(rows == null) {
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
		while(true) {
			int rowEnds = text.indexOf('\n', rowStarts);
			if(rowEnds == -1) {
				return;
			}
			rows.add(new PastedImportRow(parseRow(text.substring(rowStarts, rowEnds))));
			rowStarts = rowEnds + 1;
		}
	}

	private String[] parseRow(String row) {
		return maybeRemoveCarriageReturn(row).split(delimeter);
	}
	
	private String guessDelimeter(String headerRow) {
		if(headerRow.contains("\t")) {
			return "\t";
		} else {
			return ",";
		}
	}

	private void parseHeaders(String headers[]) {
		columns = Lists.newArrayList();
		for(int i=0;i!=headers.length;++i) {
			ImportColumnDescriptor column = new ImportColumnDescriptor();
			column.setIndex(i);
			column.setHeader(headers[i]);
			columns.add(column);
		}
	}
	
    @Override
	public List<ImportRow> getRows() {
		return rows;
	}

	private String maybeRemoveCarriageReturn(String row) {
        if(row.endsWith("\r")) {
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
