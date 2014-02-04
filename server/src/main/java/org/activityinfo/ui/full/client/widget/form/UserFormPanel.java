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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Panel to render UserForm definition.
 *
 * @author YuriyZ
 */
public class UserFormPanel extends Composite {

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

    public static interface UserFormPanelUiBinder extends UiBinder<Widget, UserFormPanel> {
    }

    private UserForm userForm;
    private UserFormInstance initialFormInstance;
    private UserFormInstance formInstance;
    private boolean readOnly = false;
    private boolean designEnabled = false;
    private final List<Handler> handlerList = Lists.newArrayList();
    //
//    private final Button addFieldButton = new Button(I18N.CONSTANTS.newField());
//    private final Button removeFieldButton = new Button(I18N.CONSTANTS.removeField());
    private final Map<Iri, FormFieldRow> controlMap = Maps.newHashMap();

    @UiField
    Button saveButton;
    @UiField
    Button resetButton;
    @UiField
    FlowPanel contentPanel;

    public UserFormPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public UserFormPanel(UserForm userForm) {
        this();
        renderForm(userForm);
    }

    /**
     * Renders user form.
     */
    public void renderForm(UserForm userForm) {
        this.userForm = userForm;
        contentPanel.clear();
        renderElements(this.userForm.getElements());
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
                    final FormFieldRow w = new FormFieldRow((FormField) element);
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

    @UiHandler("saveButton")
    public void onSave(ClickEvent event) {
        for (Handler handler : handlerList) {
            handler.onSave();
        }
    }

    @UiHandler("resetButton")
    public void onReset(ClickEvent event) {
        final List<FormField> userFormFields = userForm.getFields();
        if (initialFormInstance != null) {
            applyValue(initialFormInstance);

            final List<FormField> fieldsCopy = new ArrayList<FormField>(userFormFields);
            final Set<Iri> fieldsWithValues = initialFormInstance.getValueMap().keySet();
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

    protected void clearFields(@Nonnull List<FormField> fields) {
        for (FormField field : fields) {
            final FormFieldRow formFieldRow = controlMap.get(field.getId());
            formFieldRow.clear();
        }
    }

    public UserForm getUserForm() {
        return userForm;
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

    public void setValue(@Nonnull UserFormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        this.initialFormInstance = formInstance;
        this.formInstance = formInstance.copy();
        applyValue(formInstance);
        addValueChangeHandler(formInstance);
    }

    private void addValueChangeHandler(@Nonnull final UserFormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        for (final Map.Entry<Iri, FormFieldRow> entry : controlMap.entrySet()) {
            final IsWidget widget = entry.getValue().getFormFieldWidget();
            if (widget instanceof HasValueChangeHandlers) {
                final HasValueChangeHandlers hasValueChangeHandlers = (HasValueChangeHandlers) widget;
                hasValueChangeHandlers.addValueChangeHandler(new ValueChangeHandler() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        formInstance.set(entry.getKey(), event.getValue());
                    }
                });
            }
        }
    }

    private void applyValue(@Nonnull UserFormInstance formInstance) {
        Preconditions.checkNotNull(formInstance);
        for (Map.Entry<Iri, Object> entry : formInstance.getValueMap().entrySet()) {
            final FormFieldRow fieldRow = controlMap.get(entry.getKey());
            fieldRow.setValue(entry.getValue());
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        for (FormFieldRow row : controlMap.values()) {
            row.setReadOnly(readOnly);
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public UserFormInstance getInitialFormInstance() {
        return initialFormInstance;
    }

    public void setInitialFormInstance(UserFormInstance initialFormInstance) {
        this.initialFormInstance = initialFormInstance;
    }
}
