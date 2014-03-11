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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
import com.google.gwt.view.client.SelectionChangeEvent;
import org.activityinfo.api.client.KeyGenerator;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.i18n.I18N;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 3/5/14.
 */
public class FormFieldInlineReferenceEdit extends Composite implements HasInstances {

    private static FormFieldInlineReferenceEditBinder uiBinder = GWT
            .create(FormFieldInlineReferenceEditBinder.class);

    interface FormFieldInlineReferenceEditBinder extends UiBinder<Widget, FormFieldInlineReferenceEdit> {
    }

    public static final int MAX_INSTANCE_COUNT = 1000;
    public static final String NEW_NAME_PREFIX = I18N.CONSTANTS.newInstancePrefix();

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
    @UiField
    DivElement errorContainer;

    public FormFieldInlineReferenceEdit() {
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
        singleChoice.setValue(true);
    }

    private void initTable() {
        removeButton.setEnabled(false);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                removeButton.setEnabled(!selectionModel.getSelectedSet().isEmpty());
            }
        });

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
                validate();
            }
        });

        table.addColumn(checkColumn);
        table.addColumn(labelColumn);
        table.setColumnWidth(checkColumn, 40, Style.Unit.PX);
        table.setColumnWidth(labelColumn, 300, Style.Unit.PX);

        tableDataProvider.addDataDisplay(table);
    }

    public void updateModel() {
        final FormField formField = getFormField();
        if (formField != null) {
            formField.setCardinality(singleChoice.getValue() ? FormFieldCardinality.SINGLE : FormFieldCardinality.MULTIPLE);

            // update range
            final Set<Cuid> newRange = Sets.newHashSet();
            final List<FormInstance> instances = tableDataProvider.getList();
            for (FormInstance instance : instances) {
                newRange.add(instance.getId());
            }
            formField.setRange(newRange);
        }
    }

    @UiHandler("addButton")
    public void onAddButton(ClickEvent event) {
        final FormField formField = getFormField();
        if (formField != null) {
            final Cuid newCuid = CuidAdapter.newFormInstance();
            final FormInstance newFormInstance = new FormInstance(newCuid, getContainer().getFormPanel().getFormClass().getId());
            FormInstanceLabeler.setLabel(newFormInstance, newName());
            tableDataProvider.getList().add(newFormInstance);
            tableDataProvider.refresh();
            getContainer().fireState(false);
        }
    }

    private void validate() {
        clearError();
        validateInstanceNames();
        container.fireState(false);
    }

    public boolean isInValidState() {
        final Set<String> duplications = FormInstanceLabeler.getDuplicatedInstanceLabels(tableDataProvider.getList());
        return duplications.isEmpty();
    }

    private void validateInstanceNames() {
        final Set<String> duplications = FormInstanceLabeler.getDuplicatedInstanceLabels(tableDataProvider.getList());

        if (!duplications.isEmpty()) {
            showError(I18N.CONSTANTS.duplicateValues());

            for (FormInstance instance : tableDataProvider.getList()) {
                if (duplications.contains(FormInstanceLabeler.getLabel(instance))) {
                    markInstanceCell(instance);
                }
            }
        }
    }

    private void markInstanceCell(FormInstance instance) {
        final int index = tableDataProvider.getList().indexOf(instance);
        final NodeList<TableCellElement> cells = table.getRowElement(index).getCells();
        for (int i = 0; i < cells.getLength(); i++) {
            final TableCellElement cellElement = cells.getItem(i);
            cellElement.getStyle().setColor("#a94442");
        }
    }

    private String newName() {
        final List<String> existingNames = FormInstanceLabeler.getLabels(tableDataProvider.getList());
        for (int i = 0; i < MAX_INSTANCE_COUNT; i++) {
            final String newName = NEW_NAME_PREFIX + i;
            if (!existingNames.contains(newName)) {
                return newName;
            }
        }
        return Integer.toString(new KeyGenerator().generateInt());
    }

    @UiHandler("removeButton")
    public void onRemoveButton(ClickEvent event) {
        final Set<FormInstance> selectedSet = selectionModel.getSelectedSet();
        tableDataProvider.getList().removeAll(selectedSet);
        tableDataProvider.refresh();
        getContainer().fireState(false);
    }

    public void apply() {
        final HasInstances hasInstances = getHasInstances();
        final FormField formField = getFormField();
        tableDataProvider.setList(hasInstances != null ? hasInstances.getInstances() : Lists.<FormInstance>newArrayList());
        tableDataProvider.refresh();
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

    @Override
    public List<FormInstance> getInstances() {
        return Lists.newArrayList(tableDataProvider.getList());
    }

    public FormField getFormField() {
        if (container != null) {
            return container.getFormField();
        }
        return null;
    }

    public void showError(String errorMessage) {
        errorContainer.setInnerSafeHtml(SafeHtmlUtils.fromSafeConstant(errorMessage));
    }

    public void clearError() {
        errorContainer.setInnerHTML("");
    }

    public FormFieldInlineEdit getContainer() {
        return container;
    }

    public void setContainer(FormFieldInlineEdit editPanel) {
        this.container = editPanel;
    }
}
