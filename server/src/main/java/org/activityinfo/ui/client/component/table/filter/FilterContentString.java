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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.core.client.ProjectionKeyProvider;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.filter.Filter;
import org.activityinfo.ui.client.component.table.DataGrid;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.style.table.DataGridResources;
import org.activityinfo.ui.client.widget.TextBox;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 4/3/14.
 */
public class FilterContentString extends Composite implements FilterContent {

    public static final String FILTER_GRID_HEIGHT = "300px";


    interface FilterContentStringUiBinder extends UiBinder<HTMLPanel, FilterContentString> {
    }

    private static FilterContentStringUiBinder uiBinder = GWT.create(FilterContentStringUiBinder.class);

    private final ListDataProvider<Projection> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());

    private final FieldColumn column;
    private final DataGrid<Projection> table;
    private final DataGrid<Projection> filterGrid;
    private final List<Projection> allItems;

    @UiField
    TextBox textBox;
    @UiField
    HTMLPanel gridContainer;

    public FilterContentString(DataGrid<Projection> table, FieldColumn column) {
        DataGridResources.INSTANCE.dataGridStyle().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));

        this.table = table;
        this.column = column;

        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                filterData();
            }
        });

        final Column<Projection, Boolean> checkColumn = new Column<Projection, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Projection object) {
                return selectionModel.isSelected(object);
            }
        };

        filterGrid = new DataGrid<>(100, DataGridResources.INSTANCE);
        filterGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager
                .<Projection> createCheckboxManager());
        filterGrid.addColumn(checkColumn);
        filterGrid.addColumn(column);
        filterGrid.setColumnWidth(checkColumn, 20, Style.Unit.PX);
        filterGrid.setHeight(FILTER_GRID_HEIGHT);
        filterGrid.setAutoHeaderRefreshDisabled(true);
        filterGrid.setAutoFooterRefreshDisabled(true);

        tableDataProvider.addDataDisplay(filterGrid);
        allItems = table.getVisibleItems();
        tableDataProvider.setList(allItems);

        gridContainer.add(filterGrid);
    }

    private void filterData() {
        final String stringFilter = textBox.getValue();
        final List<Projection> toShow = Lists.newArrayList();
        for (Projection projection : allItems) {
            final String value = column.getValue(projection);
            if (!Strings.isNullOrEmpty(value) && (Strings.isNullOrEmpty(stringFilter) || value.contains(stringFilter))) {
                toShow.add(projection);
            }
        }
        tableDataProvider.setList(toShow);
    }

    @Override
    public Filter getFilter() {
        final Set<Projection> selectedSet = selectionModel.getSelectedSet();
        return null;
    }
}