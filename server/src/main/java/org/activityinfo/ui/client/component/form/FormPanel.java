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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.validation.ValidationFailure;
import org.activityinfo.core.shared.validation.ValidationUtils;
import org.activityinfo.core.shared.validation.Validator;
import org.activityinfo.core.shared.validation.ValidatorBuilder;
import org.activityinfo.core.shared.validation.widget.NotEmptyValidator;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.component.form.event.DesignStateEvent;
import org.activityinfo.ui.client.component.form.event.PersistEvent;
import org.activityinfo.ui.client.component.form.event.PersistedSuccessfullyEvent;
import org.activityinfo.ui.client.component.form.event.UpdateStateEvent;
import org.activityinfo.ui.client.util.GwtUtil;
import org.activityinfo.ui.client.widget.undo.UndoListener;
import org.activityinfo.ui.client.widget.undo.UndoManager;
import org.activityinfo.ui.client.widget.undo.UndoableCreatedEvent;
import org.activityinfo.ui.client.widget.undo.UndoableExecutedEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Panel to render FormClass definition.
 *
 * @author YuriyZ
 */
public class FormPanel extends Composite {

    private static UserFormPanelUiBinder uiBinder = GWT
            .create(UserFormPanelUiBinder.class);

    public static interface UserFormPanelUiBinder extends UiBinder<Widget, FormPanel> {
    }

    private FormClass initialFormClass;
    private FormClass formClass;
    private FormInstance initialFormInstance;
    private FormInstance formInstance;
    private ResourceLocator resourceLocator;
    private boolean readOnly = false;
    private boolean designEnabled = false;
    private ElementNode elementNode;
    private final EventBus eventBus = new SimpleEventBus();
    private final UndoManager undoManager = new UndoManager();

    @UiField
    Button saveButton;
    @UiField
    Button resetButton;
    @UiField
    FlowPanel contentPanel;
    @UiField
    DivElement errorContainer;
    @UiField
    DivElement progressDiv;
    @UiField
    Button undoButton;
    @UiField
    Button redoButton;
    @UiField
    Button addSectionButton;
    @UiField
    Button addFieldButton;
    @UiField
    FormSectionInlineEdit addSectionPanel;
    @UiField
    FormFieldInlineEdit addFieldPanel;
    @UiField
    DivElement infoContainer;

    public FormPanel(ResourceLocator resourceLocator) {
        FormPanelStyles.INSTANCE.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        this.resourceLocator = resourceLocator;
        initUndo();
        initPanels();
        initEventBusHandlers();
    }

    public FormPanel(FormClass formClass, ResourceLocator resourceLocator) {
        this(resourceLocator);
        setFormClass(formClass);
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    /**
     * Renders user form.
     */
    public void setFormClass(FormClass formClass) {
        this.formClass = formClass;
        this.initialFormClass = formClass.copy();
        this.elementNode = new ElementNode(this, contentPanel, null, formClass);
        elementNode.renderElements(this.formClass.getElements());
    }

    private void initUndo() {
        setUndoRedoButtonsState();
        undoManager.addListener(new UndoListener() {
            @Override
            public void onUndoableExecuted(UndoableExecutedEvent event) {
                setUndoRedoButtonsState();
            }

            @Override
            public void onUndoableCreated(UndoableCreatedEvent event) {
                setUndoRedoButtonsState();
            }
        });
    }

    private void setUndoRedoButtonsState() {
        undoButton.setEnabled(undoManager.canUndo());
        redoButton.setEnabled(undoManager.canRedo());
    }

    private void initEventBusHandlers() {
        eventBus.addHandler(UpdateStateEvent.TYPE, new UpdateStateEvent.Handler() {
            @Override
            public void update(UpdateStateEvent p_event) {
                fireState();
            }
        });
        eventBus.addHandler(PersistEvent.TYPE, new PersistEvent.Handler() {
            @Override
            public void persist(PersistEvent p_event) {
                save();
            }
        });
    }

    private void save() {
        clearMessages();

        final FormInstance value = getValue();
        if (value != null) {
            resourceLocator.persist(value).then(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Failed to save form instance");
                    ValidationUtils.addMessage(I18N.CONSTANTS.failedToSaveInstance(), errorContainer);
                }

                @Override
                public void onSuccess(Void result) {
                    ValidationUtils.showMessage(I18N.CONSTANTS.saved(), infoContainer);
                    eventBus.fireEvent(new PersistedSuccessfullyEvent());
                }
            });
        }
        if (isDesignEnabled()) {
            resourceLocator.persist(getFormClass()).then(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Failed to save form class");
                    ValidationUtils.addMessage(I18N.CONSTANTS.failedToSaveClass(), errorContainer);
                }

                @Override
                public void onSuccess(Void result) {
                    ValidationUtils.showMessage(I18N.CONSTANTS.saved(), infoContainer);
                    eventBus.fireEvent(new PersistedSuccessfullyEvent());
                }
            });
        }

    }

    private void clearMessages() {
        errorContainer.setInnerHTML("");
        infoContainer.setInnerHTML("");
    }

    public void fireState() {
        final Validator validator = createValidator();
        final List<ValidationFailure> failures = validator.validate();
        ValidationUtils.show(failures, errorContainer);
        saveButton.setEnabled(failures.isEmpty());
    }

    public Validator createValidator() {
        final BiMap<Cuid, FormFieldRow> ownAndChildFieldMap = elementNode.getOwnAndChildFieldMap();
        final Set<FormFieldRow> formFieldRows = ownAndChildFieldMap.values();

        final ValidatorBuilder validatorBuilder = ValidatorBuilder.instance();

        for (FormFieldRow row : formFieldRows) {
            if (row.getFormField().isRequired()) {
                final IsWidget formFieldWidget = row.getFormFieldWidget();
                if (formFieldWidget instanceof HasValue) {
                    final String controlName = row.getFormField().getLabel().getValue() + " (" + I18N.CONSTANTS.mandatory() + ")";
                    validatorBuilder.addValidator(new NotEmptyValidator((HasValue) formFieldWidget, controlName));
                }
            }
        }
        return validatorBuilder.build();
    }

    @UiHandler("saveButton")
    public void onSave(ClickEvent event) {
        beforeSave();
        eventBus.fireEvent(new PersistEvent());
    }

    protected void beforeSave() {
    }

    @UiHandler("resetButton")
    public void onReset(ClickEvent event) {
        clearMessages();
        if (isDesignEnabled()) {
            rebuildPanel(); // Completely rebuilds panel.
        } else {
            resetFormInstanceValues(); // Resets only form instance values.
        }
        fireState();
    }

    @UiHandler("undoButton")
    public void onUndo(ClickEvent event) {
        undoManager.undo();
    }

    @UiHandler("redoButton")
    public void onRedo(ClickEvent event) {
        undoManager.redo();
    }

    private void initPanels() {
        addFieldPanel.setFormPanel(this);

        addSectionPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSectionPanel.updateModel();
                elementNode.addSection(addSectionPanel.getFormSection(), 0);
            }
        });
        addFieldPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addFieldPanel.updateModel();
                elementNode.addField(addFieldPanel.createNewRow(elementNode), 0);
            }
        });
    }

    @UiHandler("addSectionButton")
    public void onAddSection(ClickEvent event) {
        addSectionPanel.applyNew();
        addSectionPanel.setVisible(true);
    }

    @UiHandler("addFieldButton")
    public void onAddField(ClickEvent event) {
        addFieldPanel.applyNew();
        addFieldPanel.setVisible(true);
    }

    /**
     * Resets only form instance values.
     */
    private void resetFormInstanceValues() {
        final List<FormField> userFormFields = formClass.getFields();
        if (initialFormInstance != null) {
            applyValue(initialFormInstance);

            final List<FormField> fieldsCopy = new ArrayList<FormField>(userFormFields);
            final Set<Cuid> fieldsWithValues = initialFormInstance.getValueMap().keySet();
            Iterables.removeIf(fieldsCopy, new Predicate<FormField>() {
                @Override
                public boolean apply(FormField input) {
                    return fieldsWithValues.contains(input.getId());
                }
            });
            elementNode.clearFields(fieldsCopy);
        } else {
            elementNode.clearFields(userFormFields);
        }
    }

    /**
     * Completely rebuilds panel.
     */
    private void rebuildPanel() {
        setFormClass(initialFormClass);
        if (initialFormInstance != null) {
            setValue(initialFormInstance);
        }
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public void setDesignEnabled(boolean designEnabled) {
        this.designEnabled = designEnabled;
        GwtUtil.setVisibleInline(designEnabled, addSectionButton.getElement(), addFieldButton.getElement());
        eventBus.fireEvent(new DesignStateEvent(designEnabled));
    }

    public boolean isDesignEnabled() {
        return designEnabled;
    }

    public FormInstance getValue() {
        return formInstance;
    }

    public void setValue(@Nonnull FormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        this.initialFormInstance = formInstance.copy();
        this.formInstance = formInstance;
        applyValue(formInstance);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fireState();
            }
        });
    }

    private void applyValue(@Nonnull FormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        final BiMap<Cuid, FormFieldRow> allFieldMap = elementNode.getOwnAndChildFieldMap();
        for (Map.Entry<Cuid, Object> entry : formInstance.getValueMap().entrySet()) {
            try {
                final FormFieldRow fieldRow = allFieldMap.get(entry.getKey());
                if (fieldRow != null) {
                    fieldRow.setValue(entry.getValue());
                } else {
                    Log.error("Form instance contains data which are not declared by form definition. formInstanceId =" +
                            formInstance.getId());
                }
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        for (FormFieldRow row : elementNode.getOwnAndChildFieldMap().values()) {
            row.setReadOnly(readOnly);
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public FormClass getInitialFormClass() {
        return initialFormClass;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public FormInstance getInitialFormInstance() {
        return initialFormInstance;
    }

    public void setInitialFormInstance(FormInstance initialFormInstance) {
        this.initialFormInstance = initialFormInstance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setSaveButtonVisible(boolean visible) {
        GwtUtil.setVisible(visible, saveButton.getElement());
    }

    public void setResetButtonVisible(boolean visible) {
        GwtUtil.setVisible(visible, resetButton.getElement());
    }
}
