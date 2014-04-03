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

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Window;
import org.activityinfo.ui.client.widget.cell.HasCellAdapter;

/**
 * @author yuriyz on 4/2/14.
 */
public class FilterHeader extends Header<String> {

    private final String headerValue;

    public FilterHeader(final String headerValue) {
        super(createCell(headerValue));
        this.headerValue = headerValue;
    }

    private static CompositeCell createCell(final String headerValue) {
        final TextCell textCell = new TextCell();
        final FilterCell actionCell = new FilterCell(new ActionCell.Delegate() {
            @Override
            public void execute(Object object) {
                Window.alert(headerValue);
            }
        });
        final HasCell headerNameCell = new HasCellAdapter(textCell) {
            @Override
            public Object getValue(Object object) {
                return headerValue;
            }
        };
        return new CompositeCell(Lists.newArrayList(
                headerNameCell,
                new HasCellAdapter(actionCell)
        ));
    }

    @Override
    public String getValue() {
        return headerValue;
    }
}