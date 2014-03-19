package org.activityinfo.ui.client.component.form;
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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.*;
import org.activityinfo.core.shared.form.has.HasInstances;
import org.activityinfo.core.shared.validation.*;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.client.callback.SuccessCallback;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.component.form.event.PersistEvent;
import org.activityinfo.ui.client.util.GwtUtil;
import org.activityinfo.ui.client.widget.TextBox;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 3/5/14.
 */
public class FormFieldInlineReferenceEdit extends Composite implements HasInstances, HasValidator {

    public static enum ReferType {
        NEW {
            @Override
            public String getLabel() {
                return I18N.CONSTANTS.newForm();
            }
        }, EXISTING {
            @Override
            public String getLabel() {
                return I18N.CONSTANTS.chooseExistingFormClass();
            }
        },
        CURRENT {
            @Override
            public String getLabel() {
                return I18N.CONSTANTS.currentFormClass();
            }
        };

        public abstract String getLabel();
    }

    private static FormFieldInlineReferenceEditBinder uiBinder = GWT
            .create(FormFieldInlineReferenceEditBinder.class);

    interface FormFieldInlineReferenceEditBinder extends UiBinder<Widget, FormFieldInlineReferenceEdit> {
    }

    public static final int MAX_INSTANCE_COUNT = 1000;
    public static final String NEW_NAME_PREFIX = I18N.CONSTANTS.newInstancePrefix();

    private final ListDataProvider<FormInstance> tableDataProvider = new ListDataProvider<>();
    private final MultiSelectionModel<FormInstance> selectionModel = new MultiSelectionModel<>(
            FormInstanceKeyProvider.getInstance());

    private final BiMap<String, Cuid> formClassLabelToCuidBiMap = HashBiMap.create();
    private final Validator validator;
    private FormFieldInlineEdit container;
    private boolean existingFormOracleIsInitialized = false;
    private boolean addedEventBusHandlers = false;
    private FormClass newFormClass = null;
    private boolean editMode;

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
    @UiField
    ListBox referType;
    @UiField
    DivElement valuesContainer;
    @UiField
    TextBox newFormName;
    @UiField
    DivElement newFormNameContainer;
    @UiField
    DivElement existingFormContainer;
    @UiField
    SuggestBox existingForm;

    public FormFieldInlineReferenceEdit() {
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
        initReferType();
        validator = createValidator();
        singleChoice.setValue(true);
        fireState();
    }

    private Validator createValidator() {
        return new Validator() {
            @Override
            public List<ValidationFailure> validate() {
                final List<ValidationFailure> failures = Lists.newArrayList();
                final ReferType selectedReferType = getSelectedReferType();
                if (selectedReferType == ReferType.NEW && getInstances().isEmpty()) {
                    final String message = ValidationUtils.format(I18N.CONSTANTS.values(), I18N.CONSTANTS.validationControlIsEmpty());
                    failures.add(new ValidationFailure(new ValidationMessage(message)));
                } else if (selectedReferType == ReferType.EXISTING && Strings.isNullOrEmpty(existingForm.getValue())) {
                    final String message = ValidationUtils.format(I18N.CONSTANTS.selectFrom(), I18N.CONSTANTS.validationControlIsEmpty());
                    failures.add(new ValidationFailure(new ValidationMessage(message)));
                }
                return failures;
            }
        };
    }

    private void initReferType() {
        final List<ReferType> values = Lists.newArrayList(ReferType.values());
        values.remove(ReferType.CURRENT);

        for (ReferType type : values) {
            referType.addItem(type.getLabel(), type.name());
        }
        referType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireState();
            }
        });
    }

    public ReferType getSelectedReferType() {
        final int selectedIndex = referType.getSelectedIndex();
        if (selectedIndex != -1) {
            final String value = referType.getValue(selectedIndex);
            if (!Strings.isNullOrEmpty(value)) {
                return ReferType.valueOf(value);
            }
        }
        return null;
    }

    private FormClass createNewFormClass() {
        if (getSelectedReferType() == ReferType.NEW) {
            final FormField newLabelField = new FormField(CuidAdapter.newFormField());
            newLabelField.setType(FormFieldType.FREE_TEXT);

            final FormClass newFormClass = new FormClass(CuidAdapter.newFormClass());
            newFormClass.setLabel(new LocalizedString(newFormName.getValue()));
            newFormClass.setParentId(getContainer().getFormPanel().getFormClass().getId());
            newFormClass.addElement(newLabelField);
            return newFormClass;
        }
        return null;
    }

    private void fireState() {
        final ReferType referType = getSelectedReferType();

        // set visibible components state
        GwtUtil.setVisible(referType == ReferType.NEW || referType == ReferType.CURRENT, valuesContainer);
        GwtUtil.setVisible(referType == ReferType.NEW, newFormNameContainer);
        GwtUtil.setVisible(referType == ReferType.EXISTING, existingFormContainer);

        if (referType != null) {
            switch (referType) {
                case CURRENT:
                    apply(); // apply current
                    break;
                case EXISTING:
                    if (!existingFormOracleIsInitialized) {
                        // init existing suggest box oracle

                        if (getContainer() != null && getContainer().getFormPanel() != null) {
                            existingFormOracleIsInitialized = true;
                            final MultiWordSuggestOracle suggestOracle = (MultiWordSuggestOracle) existingForm.getSuggestOracle();
                            getContainer().getFormPanel().getResourceLocator().queryInstances(new ClassCriteria(FormClass.CLASS_ID)).then(new SuccessCallback<List<FormInstance>>() {
                                @Override
                                public void onSuccess(List<FormInstance> result) {
                                    for (FormInstance formClass : result) {
                                        final String labelValue = formClass.getString(FormClass.LABEL_FIELD_ID);
                                        if (!Strings.isNullOrEmpty(labelValue)) {
                                            suggestOracle.add(labelValue);
                                            formClassLabelToCuidBiMap.forcePut(labelValue, formClass.getId());
                                        }
                                    }
                                }
                            });
                        }
                    }
                    break;
                case NEW:
                    tableDataProvider.setList(Lists.<FormInstance>newArrayList());
                    tableDataProvider.refresh();
                    break;
            }
        }

        if (getContainer() != null) {
            getContainer().fireState(false);
        }
    }

    private void addEventBusHandlers() {
        final FormPanel formPanel = getContainer().getFormPanel();
        if (!addedEventBusHandlers) {
            addedEventBusHandlers = true;
            formPanel.getEventBus().addHandler(PersistEvent.TYPE, new PersistEvent.Handler() {
                @Override
                public void persist(PersistEvent p_event) {
                    if (FormFieldInlineReferenceEdit.this.newFormClass != null) {
                        // persist class
                        formPanel.getResourceLocator().persist(newFormClass).then(new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                Log.error("Failed to save newFormClass from inline reference panel.", caught);
                            }

                            @Override
                            public void onSuccess(Void result) {
                                // do nothing
                            }
                        });

                    }

                    // persist new/edited form instances
                    final List<FormInstance> formInstances = tableDataProvider.getList();
                    if (isEditMode() || FormFieldInlineReferenceEdit.this.newFormClass != null) {
                        formPanel.getResourceLocator().persist(formInstances).then(new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                Log.error("Failed to save form instances from inline reference panel.", caught);
                            }

                            @Override
                            public void onSuccess(Void result) {
                                // do nothing
                            }
                        });
                    }
                }
            });
        }
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
            addEventBusHandlers();
            formField.setCardinality(singleChoice.getValue() ? FormFieldCardinality.SINGLE : FormFieldCardinality.MULTIPLE);

            // update range
            if (formField.getType() == FormFieldType.REFERENCE && !isEditMode()) {
                final ReferType selectedReferType = getSelectedReferType();
                if (selectedReferType == ReferType.NEW) {
                    newFormClass = createNewFormClass();
                    getFormField().setRange(newFormClass.getId());
                } else if (selectedReferType == ReferType.EXISTING) {
                    final String existingFormClassLabel = existingForm.getValue();
                    final Cuid cuid = formClassLabelToCuidBiMap.get(existingFormClassLabel);
                    if (cuid != null) {
                        getFormField().setRange(cuid);
                    }
                } else {
                    formField.setRange(Sets.<Cuid>newHashSet());
                }
            } else {
                formField.setRange(Sets.<Cuid>newHashSet());
            }
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

    @Override
    public Validator getValidator() {
        return validator;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (getSelectedReferType() != ReferType.CURRENT && referType.getItemCount() < ReferType.values().length) {
            final int index = 0;
            referType.insertItem(ReferType.CURRENT.getLabel(), ReferType.CURRENT.name(), index);
            referType.setSelectedIndex(index);
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), referType);
        }
    }

    public boolean isEditMode() {
        return editMode;
    }
}
