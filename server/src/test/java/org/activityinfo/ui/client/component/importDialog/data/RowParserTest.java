package org.activityinfo.ui.client.component.importDialog.data;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class RowParserTest {

    @Test
    public void simpleCsv() {

        List<PastedRow> rows = new RowParser("hello,brave,new,world\n1,2,3,4\n5,6,7,8", ',').parseRows();

        assertThat(rows.size(), equalTo(3));
        assertThat(rows.get(0).getColumnValue(0), equalTo("hello"));
        assertThat(rows.get(0).getColumnValue(1), equalTo("brave"));
        assertThat(rows.get(0).getColumnValue(3), equalTo("world"));
        assertThat(rows.get(1).getColumnValue(0), equalTo("1"));
        assertThat(rows.get(1).getColumnValue(1), equalTo("2"));
        assertThat(rows.get(1).getColumnValue(3), equalTo("4"));
        assertThat(rows.get(2).getColumnValue(3), equalTo("8"));
    }

    @Test
    public void quotedFields() {
        List<PastedRow> rows = new RowParser("\"hello, fred\",bob,\"hello, there\"\na,b,c", ',').parseRows();

        assertThat(rows.size(), equalTo(2));
        assertThat(rows.get(0).getColumnValue(0), equalTo("hello, fred"));
        assertThat(rows.get(0).getColumnValue(1), equalTo("bob"));
        assertThat(rows.get(0).getColumnValue(2), equalTo("hello, there"));
        assertThat(rows.get(1).getColumnValue(0), equalTo("a"));
    }

    @Test
    public void quotedFieldsWithNewlines() {
        List<PastedRow> rows = new RowParser(
                "1,Jane Goodall,\"304 E42nd street\nNew York, NY\nUSA\"\n" +
                "2,Richard Feynman, \"401 1st Street\nCaltech\nUSA\"", ',').parseRows();

        assertThat(rows.size(), equalTo(2));
        assertThat(rows.get(0).getColumnValue(2), equalTo("304 E42nd street\n" +
                "New York, NY\n" +
                "USA"));

        assertThat(rows.get(1).getColumnValue(0), equalTo("2"));

    }

}
