package org.activityinfo.ui.full.client.page.entry;

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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.activityinfo.api.shared.command.Month;
import org.activityinfo.api.shared.model.IndicatorRowDTO;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.full.client.util.IndicatorNumberFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid for use in the MonthlyTab
 */
class MonthlyGrid extends EditorGrid<IndicatorRowDTO> {

    private static final int MONTHS_TO_SHOW = 7;

    private static final int ROW_HEADER_WIDTH = 150;
    private static final int MONTH_COLUMN_WIDTH = 75;
    private boolean readOnly = true;

    public MonthlyGrid(GroupingStore<IndicatorRowDTO> store) {
        super(store, createColumnModel());

        setAutoExpandColumn("indicatorName");
        setLoadMask(true);
        addListener(Events.BeforeEdit, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent event) {
                if (readOnly) {
                    event.setCancelled(true);
                }
            }
        });
        
        GroupingView view = new GroupingView();
        view.setGroupRenderer(new GridGroupRenderer() {
            
            @Override
            public String render(GroupColumnData data) {
                if(data.text != null && !data.text.isEmpty()) {
                    return data.text;
                } else { 
                    return I18N.CONSTANTS.unknownGroup();
                }
            }
        });
        view.setShowGroupedColumn(false);
        view.setForceFit(true);
        setView(view);
    }
    /**
     * Updates the month headers based on the given start month
     */
    public void updateMonthColumns(Month startMonth) {
        DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMM yy");

        Month month = startMonth;
        for (int i = 0; i != MONTHS_TO_SHOW; ++i) {
            DateWrapper date = new DateWrapper(month.getYear(),
                    month.getMonth() - 1, 1);

            getColumnModel().setColumnHeader(i + 1,
                    monthFormat.format(date.asDate()));
            getColumnModel().setDataIndex(i + 1,
                    IndicatorRowDTO.propertyName(month));
            month = month.next();
        }
    }

    private static ColumnModel createColumnModel() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig indicator = new ColumnConfig("indicatorName",
                I18N.CONSTANTS.indicators(), ROW_HEADER_WIDTH);
        indicator.setSortable(false);
        indicator.setMenuDisabled(true);
        columns.add(indicator);

        for (int i = 0; i != MONTHS_TO_SHOW; ++i) {
            NumberField indicatorField = new NumberField();
            indicatorField.getPropertyEditor().setFormat(
                    IndicatorNumberFormat.INSTANCE);

            ColumnConfig valueColumn = new ColumnConfig("month" + i, "", MONTH_COLUMN_WIDTH);
            valueColumn.setNumberFormat(IndicatorNumberFormat.INSTANCE);
            valueColumn.setEditor(new CellEditor(indicatorField));
            valueColumn.setSortable(false);
            valueColumn.setMenuDisabled(true);

            columns.add(valueColumn);
        }

        return new ColumnModel(columns);
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
