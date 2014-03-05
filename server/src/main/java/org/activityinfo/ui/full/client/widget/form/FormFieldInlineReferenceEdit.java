package org.activityinfo.ui.full.client.widget.form;
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

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormInstanceLabeler;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.style.TransitionUtil;

/**
 * @author yuriyz on 3/5/14.
 */
public class FormFieldInlineReferenceEdit extends Composite {

    private static FormFieldInlineReferenceEditBinder uiBinder = GWT
            .create(FormFieldInlineReferenceEditBinder.class);

    interface FormFieldInlineReferenceEditBinder extends UiBinder<Widget, FormFieldInlineReferenceEdit> {
    }

    private FormFieldInlineEdit container;
    private final ListDataProvider<FormInstance> tableDataProvider = new ListDataProvider<>();

    @UiField
    CellTable<FormInstance> table;

    public FormFieldInlineReferenceEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {

        // Create a Pager to control the table.
        final SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager tablePager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        tablePager.setDisplay(table);

        // create columns
        final Column<FormInstance, String> labelColumn = new Column<FormInstance, String>(
                new EditTextCell()) {
            @Override
            public String getValue(FormInstance instance) {
                return FormInstanceLabeler.getLabel(instance);
            }
        };
        labelColumn.setSortable(false);
        labelColumn.setFieldUpdater(new FieldUpdater<FormInstance, String>() {
            @Override
            public void update(int index, FormInstance object, String value) {
                // Called when the user changes the value.
                FormInstanceLabeler.setLabel(object, value);
                tableDataProvider.refresh();
            }
        });

        table.addColumn(labelColumn);
        table.setColumnWidth(labelColumn, 50, Style.Unit.PCT);

        tableDataProvider.addDataDisplay(table);
    }

    public void apply() {
        final HasInstances hasInstances = getHasInstances();
        if (hasInstances != null) {
            tableDataProvider.setList(hasInstances.getInstances());
            tableDataProvider.refresh();
        }
    }

    public HasInstances getHasInstances() {
        if (container != null && container.getRow() != null && container.getRow().getFormFieldWidget() instanceof HasInstances) {
            return (HasInstances) container.getRow().getFormFieldWidget();
        }
        return null;
    }

    public FormFieldInlineEdit getContainer() {
        return container;
    }

    public void setContainer(FormFieldInlineEdit editPanel) {
        this.container = editPanel;
    }
}
