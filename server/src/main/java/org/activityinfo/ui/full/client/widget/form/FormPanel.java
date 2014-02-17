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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.undo.UndoListener;
import org.activityinfo.ui.full.client.widget.undo.UndoManager;
import org.activityinfo.ui.full.client.widget.undo.UndoableCreatedEvent;
import org.activityinfo.ui.full.client.widget.undo.UndoableExecutedEvent;

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

    public static interface Handler {
        public void onSave();
    }

    public static interface SectionTemplate extends SafeHtmlTemplates {
        @Template("<h3>{0}</h3><hr/>")
        SafeHtml title(String label);
    }

    private static final SectionTemplate SECTION_TEMPLATE = GWT.create(SectionTemplate.class);

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
    private final List<Handler> handlerList = Lists.newArrayList();
    private final UndoManager undoManager = new UndoManager();

    //    private final Button addFieldButton = new Button(I18N.CONSTANTS.newField());
//    private final Button removeFieldButton = new Button(I18N.CONSTANTS.removeField());
    private final BiMap<Cuid, FormFieldRow> controlMap = HashBiMap.create();

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

    public FormPanel(ResourceLocator resourceLocator) {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        this.resourceLocator = resourceLocator;
        initUndo();
    }

    public FormPanel(FormClass formClass, ResourceLocator resourceLocator) {
        this(resourceLocator);
        renderForm(formClass);
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    /**
     * Renders user form.
     */
    public void renderForm(FormClass formClass) {
        this.formClass = formClass;
        this.initialFormClass = formClass.copy();
        contentPanel.clear();
        renderElements(this.formClass.getElements());
    }

    /**
     * Renders form element recursively.
     *
     * @param elements elements to render
     */
    private void renderElements(List<FormElement> elements) {
        if (elements != null && !elements.isEmpty()) {
            for (FormElement element : elements) {
                if (element instanceof FormField) {
                    final FormFieldRow w = new FormFieldRow((FormField) element, this);
                    contentPanel.add(w);
                    controlMap.put(element.getId(), w);
                } else if (element instanceof FormSection) {
                    final FormSection section = (FormSection) element;
                    contentPanel.add(new HTML(SECTION_TEMPLATE.title(section.getLabel().getValue())));
                    renderElements(section.getElements());
                }
            }
        }
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

    public void addHandler(Handler handler) {
        handlerList.add(handler);
    }

    @UiHandler("saveButton")
    public void onSave(ClickEvent event) {
        for (Handler handler : handlerList) {
            handler.onSave();
        }
    }

    @UiHandler("resetButton")
    public void onReset(ClickEvent event) {
        if (isDesignEnabled()) {
            rebuildPanel(); // Completely rebuilds panel.
        } else {
            resetFormInstanceValues(); // Resets only form instance values.
        }
    }

    @UiHandler("undoButton")
    public void onUndo(ClickEvent event) {
        undoManager.undo();
    }

    @UiHandler("redoButton")
    public void onRedo(ClickEvent event) {
        undoManager.redo();
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
            clearFields(fieldsCopy);
        } else {
            clearFields(userFormFields);
        }
    }

    /**
     * Completely rebuilds panel.
     */
    private void rebuildPanel() {
        renderForm(initialFormClass);
        if (initialFormInstance != null) {
            setValue(initialFormInstance);
        }
    }

    protected void clearFields(@Nonnull List<FormField> fields) {
        for (FormField field : fields) {
            final FormFieldRow formFieldRow = controlMap.get(field.getId());
            if (formFieldRow != null) {
                formFieldRow.clear();
            }
        }
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public void setDesignEnabled(boolean designEnabled) {
        this.designEnabled = designEnabled;
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
        addValueChangeHandler(formInstance);
    }

    private void addValueChangeHandler(@Nonnull final FormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        for (final Map.Entry<Cuid, FormFieldRow> entry : controlMap.entrySet()) {
            final IsWidget widget = entry.getValue().getFormFieldWidget();
            if (widget instanceof HasValueChangeHandlers) {
                final HasValueChangeHandlers hasValueChangeHandlers = (HasValueChangeHandlers) widget;
                hasValueChangeHandlers.addValueChangeHandler(new ValueChangeHandler() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        final Object oldValue = formInstance.get(entry.getKey());
                        pushUndoOperation(oldValue, event);
                        formInstance.set(entry.getKey(), event.getValue());
                    }
                });
            }
        }
    }

    private void pushUndoOperation(Object oldValue, ValueChangeEvent event) {
        // todo
    }

    private void applyValue(@Nonnull FormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        for (Map.Entry<Cuid, Object> entry : formInstance.getValueMap().entrySet()) {
            final FormFieldRow fieldRow = controlMap.get(entry.getKey());
            if (fieldRow != null) {
                fieldRow.setValue(entry.getValue());
            } else {
                Log.error("Form instance contains data which are not decrared by form definition. instanceid =" +
                        formInstance.getId());
            }
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        for (FormFieldRow row : controlMap.values()) {
            row.setReadOnly(readOnly);
        }
    }

    public void showError(String errorMessage) {
        errorContainer.setInnerSafeHtml(SafeHtmlUtils.fromSafeConstant(errorMessage));
    }

    public void clearError() {
        errorContainer.setInnerHTML("");
    }

    public void removeRow(FormFieldRow formFieldRow) {
        contentPanel.remove(formFieldRow);
        controlMap.remove(controlMap.inverse().get(formFieldRow));
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
}
