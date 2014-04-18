package org.activityinfo.ui.client.importer;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.getResource;

/**
 * @author yuriyz on 4/18/14.
 */
public class PastedTableParserTest {

    @Test
    public void parser() throws IOException {
        PastedTable pastedTable = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));
        final List<SourceColumn> columns = pastedTable.getColumns();
        final List<SourceRow> rows = pastedTable.getRows();

        Assert.assertEquals(columns.size(), 46);
        Assert.assertEquals(rows.size(), 63);
    }
}
