package org.activityinfo.ui.client.component.table.filter;
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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.component.table.InstanceTableStyle;

/**
 * @author yuriyz on 4/2/14.
 */
public class FilterGridHeaderBuilder extends AbstractHeaderOrFooterBuilder<Projection> {

    /**
     * Create a new FilterGridHeaderBuilder for the header section.
     *
     * @param table the table being built
     */
    public FilterGridHeaderBuilder(AbstractCellTable<Projection> table) {
        super(table, false);
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {

        // early exit...
        if (getTable().getColumnCount() == 0) {
            return false;
        }

        renderHeaderRow();

        return true;
    }

    private void renderHeaderRow() {
        TableRowBuilder tr = startRow();

        int currentColumn;
        int columnCount = getTable().getColumnCount();
        for (currentColumn = 0; currentColumn < columnCount; currentColumn++) {
            FilterHeader header = (FilterHeader) getHeader(currentColumn);
            FieldColumn column = (FieldColumn) getTable().getColumn(currentColumn);

            // Render the header.
            TableCellBuilder th = tr.startTH().className(InstanceTableStyle.INSTANCE.header());
            enableColumnHandlers(th, column);

            // Build the header.
            Cell.Context context = new Cell.Context(0, currentColumn, null);
            renderHeader(th, context, header);

            th.endTH();
        }
        tr.end();
    }
}
