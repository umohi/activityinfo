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

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.*;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;
import java.util.Map;

/**
 * Panel to render UserForm definition.
 *
 * @author YuriyZ
 */
public class UserFormPanel extends Composite {

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
    private FormInstance formInstance;
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

    public UserFormPanel(UserForm userForm, ResourceLocator resourceLocator) {
        this();
        this.userForm = userForm;
        renderForm();
    }

    /**
     * Renders user form.
     */
    public void renderForm() {
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
        // todo
    }

    @UiHandler("resetButton")
    public void onReset(ClickEvent event) {
        // todo
    }

    public UserForm getUserForm() {
        return userForm;
    }

    public FormInstance getFormInstance() {
        return null;
    }

    public void setDesignEnabled(boolean designEnabled) {

    }

    public void setValue(FormInstance formInstance) {
        this.formInstance = formInstance;
    }

    public void setReadOnly(boolean readOnly) {

    }
}
