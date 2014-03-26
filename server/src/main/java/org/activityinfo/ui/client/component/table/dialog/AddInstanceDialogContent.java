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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.component.table.InstanceTableView;
import org.activityinfo.ui.client.widget.ButtonWithSize;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 3/24/14.
 */
public class AddInstanceDialogContent extends Composite {

    interface AddInstanceDialogContentUiBinder extends UiBinder<HTMLPanel, AddInstanceDialogContent> {
    }

    private static AddInstanceDialogContentUiBinder uiBinder = GWT.create(AddInstanceDialogContentUiBinder.class);

    private final Map<FieldColumn, AddInstanceDialogRow> rowMap = Maps.newHashMap();
    private final InstanceTableView tableView;

    @UiField
    FormElement form;
    @UiField
    ButtonWithSize addButton;

    public AddInstanceDialogContent(InstanceTableView tableView, AddInstanceDialog dialog) {
        initWidget(uiBinder.createAndBindUi(this));
        this.tableView = tableView;
        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onOk();
            }
        });

        final List<FieldColumn> selectedColumns = tableView.getSelectedColumns();
        for (FieldColumn column : selectedColumns) {
            addColumn(column);
        }
    }

    @UiHandler("addButton")
    public void onAddColumn(ClickEvent event) {
        final AddColumnDialog dialog = new AddColumnDialog(tableView.getColumns(), Lists.newArrayList(rowMap.keySet()));
        dialog.show();
        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addColumns(dialog.getContent().getSelectionModel().getSelectedSet());
            }
        });
    }

    private void addColumns(Set<FieldColumn> selectedSet) {
        for (FieldColumn column : selectedSet) {
            addColumn(column);
        }
    }

    private void addColumn(FieldColumn column) {
        if (!rowMap.containsKey(column)) {
            final AddInstanceDialogRow row = new AddInstanceDialogRow(column, tableView.getResourceLocator());
            rowMap.put(column, row);
            form.appendChild(row.getElement());
        }
    }

    private void onOk() {
        // todo
    }

}
