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

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.Set;

/**
 * @author yuriyz on 3/5/14.
 */
public class FormFieldInlineReferenceEdit extends Composite {

    private static FormFieldInlineReferenceEditBinder uiBinder = GWT
            .create(FormFieldInlineReferenceEditBinder.class);

    interface FormFieldInlineReferenceEditBinder extends UiBinder<Widget, FormFieldInlineReferenceEdit> {
    }

    private final ListDataProvider<FormInstance> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FormInstance> selectionModel = new MultiSelectionModel<>(
            FormInstanceKeyProvider.getInstance());

    private FormFieldInlineEdit container;

    @UiField
    CellTable<FormInstance> table;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    RadioButton multipleChoice;
    @UiField
    RadioButton singleChoice;

    public FormFieldInlineReferenceEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
    }

    private void initTable() {
        table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FormInstance>createCheckboxManager());
        //table.setSelectionEnabled(true) to enable

        // Create a Pager to control the table.
        final SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager tablePager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        tablePager.setDisplay(table);

        // create columns

        final Column<FormInstance, Boolean> checkColumn = new Column<FormInstance, Boolean>(
                new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(FormInstance object) {
                return selectionModel.isSelected(object);
            }
        };

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

        table.addColumn(checkColumn);
        table.addColumn(labelColumn);
        table.setColumnWidth(checkColumn, 40, Style.Unit.PX);
        table.setColumnWidth(labelColumn, 300, Style.Unit.PX);

        tableDataProvider.addDataDisplay(table);
    }

    public void onOkClick() {
        final FormField formField = getFormField();
        if (formField != null) {
            formField.setCardinality(singleChoice.getValue() ? FormFieldCardinality.SINGLE : FormFieldCardinality.MULTIPLE);
        }
        // todo - instance handling
    }

    @UiHandler("addButton")
    public void onAddButton(ClickEvent event) {
        final FormField formField = getFormField();
        if (formField != null) {
            final Cuid newCuid = CuidAdapter.newFormInstance();
            final FormInstance newFormInstance = new FormInstance(newCuid, formField.getId());
            FormInstanceLabeler.setLabel(newFormInstance, "New"); // todo
            tableDataProvider.getList().add(newFormInstance);
            tableDataProvider.refresh();
        }
    }

    @UiHandler("removeButton")
    public void onRemoveButton(ClickEvent event) {
        final Set<FormInstance> selectedSet = selectionModel.getSelectedSet();
        tableDataProvider.getList().removeAll(selectedSet);
        tableDataProvider.refresh();
    }

    public void apply() {
        final HasInstances hasInstances = getHasInstances();
        final FormField formField = getFormField();
        if (hasInstances != null) {
            tableDataProvider.setList(hasInstances.getInstances());
            tableDataProvider.refresh();
        }
        if (formField != null && formField.getCardinality() != null) {
            switch (formField.getCardinality()) {
                case SINGLE:
                    singleChoice.setValue(true);
                    break;
                case MULTIPLE:
                    multipleChoice.setValue(true);
                    break;
            }
        }
    }

    public HasInstances getHasInstances() {
        if (container != null && container.getRow() != null && container.getRow().getFormFieldWidget() instanceof HasInstances) {
            return (HasInstances) container.getRow().getFormFieldWidget();
        }
        return null;
    }

    public FormField getFormField() {
        if (container != null) {
            return container.getFormField();
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
