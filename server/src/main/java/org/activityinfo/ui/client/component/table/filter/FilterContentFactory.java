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

import org.activityinfo.core.shared.Projection;
import org.activityinfo.ui.client.component.table.DataGrid;
import org.activityinfo.ui.client.component.table.FieldColumn;

/**
 * @author yuriyz on 4/3/14.
 */
public class FilterContentFactory {

    private FilterContentFactory() {
    }

    public static FilterContent create(DataGrid<Projection> table, FieldColumn column) {
        switch (column.getNode().getFieldType()) {
            case FREE_TEXT:
            case NARRATIVE:
                return new FilterContentString(table, column);
        }
        return new FilterContentString(table, column);
    }
}