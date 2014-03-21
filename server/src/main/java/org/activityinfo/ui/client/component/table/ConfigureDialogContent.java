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

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.key.ResourceKeyProvider;

/**
 * @author yuriyz on 3/21/14.
 */
public class ConfigureDialogContent extends Composite {

    interface ConfigureDialogContentUiBinder extends UiBinder<HTMLPanel, ConfigureDialogContent> {
    }

    private static ConfigureDialogContentUiBinder uiBinder = GWT.create(ConfigureDialogContentUiBinder.class);

    private final InstanceTableView tableView;
    private final ConfigureDialog dialog;

    private final ListDataProvider<FormField> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FormField> selectionModel = new MultiSelectionModel<>(
            ResourceKeyProvider.FORM_FIELD_KEY_PROVIDER);

    @UiField
    CellTable table;

    public ConfigureDialogContent(InstanceTableView tableView, ConfigureDialog dialog) {
        this.tableView = tableView;
        this.dialog = dialog;
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {
        table.setSelectionModel(selectionModel);
        tableDataProvider.addDataDisplay(table);

        final Column<FormField, Boolean> checkColumn = new Column<FormField, Boolean>(
                new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(FormField object) {
                return selectionModel.isSelected(object);
            }
        };

        final Column<FormField, String> labelColumn = new Column<FormField, String>(
                new TextCell()) {
            @Override
            public String getValue(FormField object) {
                return object.getLabel().getValue();
            }
        };
        labelColumn.setSortable(false);

        table.addColumn(checkColumn);
        table.addColumn(labelColumn);
        table.setColumnWidth(checkColumn, 40, Style.Unit.PX);
        table.setColumnWidth(labelColumn, 300, Style.Unit.PX);
    }

}


