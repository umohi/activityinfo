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

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.core.shared.form.key.SelfKeyProvider;
import org.activityinfo.ui.client.style.table.CellTableResources;

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

    private final ListDataProvider<FieldColumn> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FieldColumn> selectionModel = new MultiSelectionModel<FieldColumn>(
            new SelfKeyProvider<FieldColumn>());

    private CellTable<FieldColumn> table;

    @UiField
    HTMLPanel tableContainer;

    public ConfigureDialogContent(InstanceTableView tableView, ConfigureDialog dialog) {
        this.tableView = tableView;
        initTable();
        initWidget(uiBinder.createAndBindUi(this));
        tableContainer.add(table);

        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onOk();
            }
        });
    }

    private void initTable() {
        final Column<FieldColumn, Boolean> checkColumn = new Column<FieldColumn, Boolean>(
                new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(FieldColumn object) {
                return selectionModel.isSelected(object);
            }
        };

        final Column<FieldColumn, String> labelColumn = new Column<FieldColumn, String>(
                new TextCell()) {
            @Override
            public String getValue(FieldColumn object) {
                return object.getHeader();
            }
        };
        labelColumn.setSortable(false);

        table = new CellTable<>(10, CellTableResources.INSTANCE);
        table.setWidth("100%", true);
        table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FieldColumn>createCheckboxManager());
        table.setAutoHeaderRefreshDisabled(true);
        table.setAutoFooterRefreshDisabled(true);
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);
        table.addColumn(checkColumn);
        table.addColumn(labelColumn);
        table.setColumnWidth(checkColumn, 40, Style.Unit.PX);
        table.setColumnWidth(labelColumn, 100, Style.Unit.PCT);

        tableDataProvider.addDataDisplay(table);
        tableDataProvider.setList(tableView.getColumns());
        tableDataProvider.refresh();

        for (FieldColumn column : tableView.getSelectedColumns()) {
            selectionModel.setSelected(column, true);
        }
//        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//            @Override
//            public void onSelectionChange(SelectionChangeEvent event) {
//            }
//        });
    }

    private void onOk() {
        tableView.setSelectedColumns(Lists.newArrayList(selectionModel.getSelectedSet()));
    }

}


