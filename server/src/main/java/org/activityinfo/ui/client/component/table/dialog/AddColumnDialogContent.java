package org.activityinfo.ui.client.component.table.dialog;
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
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.core.shared.form.key.SelfKeyProvider;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.style.table.DataGridResources;
import org.activityinfo.ui.client.widget.TextBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuriyz on 3/26/14.
 */
public class AddColumnDialogContent extends Composite {

    interface AddColumnDialogContentUiBinder extends UiBinder<HTMLPanel, AddColumnDialogContent> {
    }

    private static AddColumnDialogContentUiBinder uiBinder = GWT.create(AddColumnDialogContentUiBinder.class);

    private final ListDataProvider<FieldColumn> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FieldColumn> selectionModel = new MultiSelectionModel<>(
            new SelfKeyProvider<FieldColumn>());

    @UiField
    TextBox filterColumnTable;
    @UiField
    HTMLPanel columnTableContainer;

    public AddColumnDialogContent(final List<FieldColumn> allColumns, final List<FieldColumn> visibleColumns) {
        DataGridResources.INSTANCE.dataGridStyle().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));

        final DataGrid<FieldColumn> table = createTable();
        table.setSelectionModel(selectionModel);
        table.setRowStyles(new RowStyles<FieldColumn>() {
            @Override
            public String getStyleNames(FieldColumn row, int rowIndex) {
                if (visibleColumns.contains(row)) {
                    return "row-disabled";
                }
                return null;
            }
        });
        tableDataProvider.addDataDisplay(table);
        tableDataProvider.setList(allColumns);
        tableDataProvider.refresh();
        columnTableContainer.add(table); // add table

        filterColumnTable.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                final String value = filterColumnTable.getValue();
                final ArrayList<FieldColumn> columnsToShow = Lists.newArrayList();
                for (FieldColumn column : allColumns) {
                    final String headerLowercased = column.getHeader().toLowerCase();
                    if (Strings.isNullOrEmpty(value) || headerLowercased.contains(value.toLowerCase())) {
                        columnsToShow.add(column);
                    }
                }

                tableDataProvider.setList(columnsToShow);
                tableDataProvider.refresh();
            }
        });

    }

    public MultiSelectionModel<FieldColumn> getSelectionModel() {
        return selectionModel;
    }

    private static DataGrid<FieldColumn> createTable() {
        final Column<FieldColumn, String> labelColumn = new Column<FieldColumn, String>(
                new TextCell()) {
            @Override
            public String getValue(FieldColumn object) {
                return object.getHeader();
            }
        };
        labelColumn.setSortable(false);

        final DataGrid<FieldColumn> table = new DataGrid<>(10, DataGridResources.INSTANCE);
        table.setHeight("300px"); // need to avoid height hardcode
        table.setEmptyTableWidget(new Label());
        table.setAutoHeaderRefreshDisabled(true);
        table.setAutoFooterRefreshDisabled(true);
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);
        table.addColumn(labelColumn);
        table.setColumnWidth(labelColumn, 100, Style.Unit.PCT);
        return table;
    }
}
