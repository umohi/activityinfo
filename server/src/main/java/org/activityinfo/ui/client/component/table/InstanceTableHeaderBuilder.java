package org.activityinfo.ui.client.component.table;
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.DefaultHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Header;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.ui.client.component.table.action.ButtonActionCell;
import org.activityinfo.ui.client.component.table.action.TableHeaderAction;

/**
 * @author yuriyz on 4/2/14.
 */
public class InstanceTableHeaderBuilder extends DefaultHeaderOrFooterBuilder<Projection> {

    private final InstanceTable table;

    /**
     * Create a new InstanceTableHeaderBuilder for the header section.
     *
     * @param table the table being built
     */
    public InstanceTableHeaderBuilder(InstanceTable table) {
        super(table.getTable(), false);
        this.table = table;
    }

    @Override
    protected boolean buildHeaderOrFooterImpl() {
        int columnCount = getTable().getColumnCount();
        if (columnCount == 0) {
            // Nothing to render;
            return false;
        }

        buildActionRow(columnCount);
        super.buildHeaderOrFooterImpl();
        return true;
    }

    public Header<?> getHeader(Element elem) {
        return super.getHeader(elem);
    }

    private void buildActionRow(int columnCount) {
        AbstractCellTable.Style style = getTable().getResources().style();

        TableRowBuilder tr = startRow();
        TableCellBuilder th = tr.startTH().colSpan(columnCount).className(style.header());

        final SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(SafeHtmlUtils.fromString(table.getRootFormClass().getLabel().getValue()));
        sb.append(SafeHtmlUtils.fromTrustedString("&nbsp;"));
        for (TableHeaderAction buttonAction : table.getHeaderActions()) {
            final ButtonActionCell cell = new ButtonActionCell(buttonAction);
            cell.render(new Cell.Context(0, 0, table), "", sb);
            sb.append(SafeHtmlUtils.fromTrustedString("&nbsp;"));
        }
        th.html(sb.toSafeHtml());

        th.endTH();
        tr.endTR();
    }
}
