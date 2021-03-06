package org.activityinfo.ui.client.widget;
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

import com.google.common.collect.Maps;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.*;
import org.activityinfo.core.shared.Pair;

import java.util.Map;

/**
 * @author yuriyz on 4/7/14.
 */
public class CellTableHeaderWidthApplier {

    private final CellTable table;

    // width information
    private final Map<Integer, Integer> headerRowToWidthMap = Maps.newHashMap();
    private final Map<Pair<Integer, Integer>, Integer> headerCellToWidthMap = Maps.newHashMap();
    private int headerWidth;

    public CellTableHeaderWidthApplier(final CellTable table) {
        this.table = table;
    }

    public void restoreHeaderWidthInformation() {
        setHeaderWidthInformation(true);
    }

    public void clearHeaderWidthInformation() {
        setHeaderWidthInformation(false);
    }

    private void setHeaderWidthInformation(boolean shouldAffix) {

        // header
        if (shouldAffix) {
            table.getTableHeadElement().getStyle().setWidth(headerWidth, Style.Unit.PX);
        } else {
            table.getTableHeadElement().getStyle().clearWidth();
        }

        final NodeList<TableRowElement> headerRows = table.getTableHeadElement().getRows();
        for (int i = 0; i < headerRows.getLength(); i++) {
            final TableRowElement row = headerRows.getItem(i);

            // rows
            if (shouldAffix) {
                row.getStyle().setWidth(headerRowToWidthMap.get(i), Style.Unit.PX);
            } else {
                row.getStyle().clearWidth();
            }

            // cells
            final NodeList<TableCellElement> cells = row.getCells();
            for (int j = 0; j < cells.getLength(); j++) {
                final TableCellElement cell = cells.getItem(j);

                if (shouldAffix) {
                    cell.getStyle().setWidth(headerCellToWidthMap.get(new Pair<>(i, j)), Style.Unit.PX);
                } else {
                    cell.getStyle().clearWidth();
                }
            }
        }
    }

    public void saveHeaderWidthInformation() {
        headerWidth = table.getTableHeadElement().getOffsetWidth();
        final NodeList<TableRowElement> headerRows = table.getTableHeadElement().getRows();
        for (int i = 0; i < headerRows.getLength(); i++) {
            final TableRowElement row = headerRows.getItem(i);
            headerRowToWidthMap.put(i, row.getOffsetWidth());

            final NodeList<TableCellElement> cells = row.getCells();
            for (int j = 0; j < cells.getLength(); j++) {
                final TableCellElement cell = cells.getItem(j);
                headerCellToWidthMap.put(new Pair<>(i, j), cell.getOffsetWidth());
            }
        }
    }

    public void setTrWidth(TableRowBuilder tr, int rowIndex) {
        final Integer integer = headerRowToWidthMap.get(rowIndex);
        tr.attribute("style", "width: " + integer + "px");
    }

    public void setTdWidth(TableCellBuilder td, int rowIndex, int columnIndex) {
        final Integer integer = headerCellToWidthMap.get(new Pair<>(rowIndex, columnIndex));
        td.attribute("style", "width: " + integer + "px");
    }
}
