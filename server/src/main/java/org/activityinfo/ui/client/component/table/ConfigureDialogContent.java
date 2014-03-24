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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.activityinfo.core.shared.form.key.SelfKeyProvider;
import org.activityinfo.ui.client.style.table.DataGridResources;
import org.activityinfo.ui.client.widget.ButtonWithSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author yuriyz on 3/21/14.
 */
public class ConfigureDialogContent extends Composite {

    private static final Logger LOGGER = Logger.getLogger(ConfigureDialogContent.class.getName());

    interface ConfigureDialogContentUiBinder extends UiBinder<HTMLPanel, ConfigureDialogContent> {
    }

    private static ConfigureDialogContentUiBinder uiBinder = GWT.create(ConfigureDialogContentUiBinder.class);

    private final InstanceTableView tableView;

    private final ListDataProvider<FieldColumn> selectedTableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FieldColumn> selectedSelectionModel = new MultiSelectionModel<FieldColumn>(
            new SelfKeyProvider<FieldColumn>());
    private final DataGrid<FieldColumn> selectedTable;

    private final ListDataProvider<FieldColumn> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FieldColumn> selectionModel = new MultiSelectionModel<FieldColumn>(
            new SelfKeyProvider<FieldColumn>());
    private final DataGrid<FieldColumn> table;

    @UiField
    HTMLPanel selectedColumnTableContainer;
    @UiField
    HTMLPanel columnTableContainer;
    @UiField
    ButtonWithSize leftButton;
    @UiField
    ButtonWithSize rightButton;
    @UiField
    TextBox filterColumnTable;
    @UiField
    ButtonWithSize upButton;
    @UiField
    ButtonWithSize downButton;

    public ConfigureDialogContent(final InstanceTableView tableView, ConfigureDialog dialog) {
        this.tableView = tableView;

        DataGridResources.INSTANCE.dataGridStyle().ensureInjected();

        selectedTable = createTable();
        selectedTable.setSelectionModel(selectedSelectionModel);
        selectedTableDataProvider.addDataDisplay(selectedTable);
        selectedTableDataProvider.setList(tableView.getSelectedColumns());
        selectedTableDataProvider.refresh();

        final List<FieldColumn> allColumns = Lists.newArrayList(tableView.getColumns());
        allColumns.removeAll(tableView.getSelectedColumns());

        table = createTable();
        table.setSelectionModel(selectionModel);
        tableDataProvider.addDataDisplay(table);
        tableDataProvider.setList(allColumns);
        tableDataProvider.refresh();

        initWidget(uiBinder.createAndBindUi(this));
        selectedColumnTableContainer.add(selectedTable);
        columnTableContainer.add(table);

        setMoveLeftButtonState();
        setMoveRightButtonState();
        setUpButtonState();
        setDownButtonState();

        selectedSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                setMoveRightButtonState();
                setMoveLeftButtonState();
                setDownButtonState();
                setUpButtonState();
            }
        });
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                setMoveLeftButtonState();
                setMoveRightButtonState();
            }
        });
        filterColumnTable.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                final String value = filterColumnTable.getValue();
                final ArrayList<FieldColumn> columnsToShow = Lists.newArrayList();
                for (FieldColumn column : tableView.getColumns()) {
                    final String headerLowercased = column.getHeader().toLowerCase();
                    if (Strings.isNullOrEmpty(value) || headerLowercased.contains(value.toLowerCase())) {
                        columnsToShow.add(column);
                    }
                }
                columnsToShow.removeAll(selectedTableDataProvider.getList());
                tableDataProvider.setList(columnsToShow);
                tableDataProvider.refresh();
            }
        });

        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onOk();
            }
        });
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

    private void onOk() {
        tableView.setSelectedColumns(Lists.newArrayList(selectedTableDataProvider.getList()));
    }

    public void setMoveLeftButtonState() {
        leftButton.setEnabled(!selectionModel.getSelectedSet().isEmpty());
    }

    public void setMoveRightButtonState() {
        rightButton.setEnabled(!selectedSelectionModel.getSelectedSet().isEmpty());
    }

    public void setUpButtonState() {
        boolean enabled = false;
        if (!selectedSelectionModel.getSelectedSet().isEmpty()) {
            int minIndex = -1;
            for (FieldColumn column : selectedSelectionModel.getSelectedSet()) {
                final int indexOf = selectedTableDataProvider.getList().indexOf(column);
                if (minIndex > indexOf || minIndex == -1) {
                    minIndex = indexOf;
                }
            }
            if (minIndex > 0) {
                enabled = true;
            }
        }

        upButton.setEnabled(enabled);
    }

    public void setDownButtonState() {
        boolean enabled = false;
        if (!selectedSelectionModel.getSelectedSet().isEmpty()) {
            int maxIndex = -1;
            for (FieldColumn column : selectedSelectionModel.getSelectedSet()) {
                final int indexOf = selectedTableDataProvider.getList().indexOf(column);
                if (maxIndex < indexOf || maxIndex == -1) {
                    maxIndex = indexOf;
                }
            }
            if (maxIndex < selectedTableDataProvider.getList().size()) {
                enabled = true;
            }
        }

        downButton.setEnabled(enabled);
    }

    @UiHandler("leftButton")
    public void onMoveLeft(ClickEvent event) {
        final Set<FieldColumn> set = selectionModel.getSelectedSet();

        tableDataProvider.getList().removeAll(set);
        tableDataProvider.refresh();

        selectedTableDataProvider.getList().addAll(set);
        selectedTableDataProvider.refresh();

        setMoveLeftButtonState();
    }

    @UiHandler("rightButton")
    public void onMoveRight(ClickEvent event) {
        final Set<FieldColumn> set = selectedSelectionModel.getSelectedSet();

        tableDataProvider.getList().addAll(set);
        tableDataProvider.refresh();

        selectedTableDataProvider.getList().removeAll(set);
        selectedTableDataProvider.refresh();

        setMoveRightButtonState();
    }

    @UiHandler("upButton")
    public void onUp(ClickEvent event) {
        final Set<FieldColumn> set = selectedSelectionModel.getSelectedSet();
        if (set != null) {
            final ArrayList<FieldColumn> copy = Lists.newArrayList(selectedTableDataProvider.getList());
            for (FieldColumn column : set) {
                final int index = copy.indexOf(column);
                if (index > 0) {
                    Collections.swap(copy, index, index - 1);
                }
            }
            selectedTableDataProvider.setList(copy);
        }
        setUpButtonState();
    }

    @UiHandler("downButton")
    public void onDown(ClickEvent event) {
        final Set<FieldColumn> set = selectedSelectionModel.getSelectedSet();
        if (set != null) {
            final ArrayList<FieldColumn> copy = Lists.newArrayList(selectedTableDataProvider.getList());
            final int size = copy.size();
            for (FieldColumn column : set) {
                final int index = copy.indexOf(column);
                if (index < (size - 1)) {
                    Collections.swap(copy, index, index + 1);
                }
            }
            selectedTableDataProvider.setList(copy);
        }
        setDownButtonState();
    }
}


