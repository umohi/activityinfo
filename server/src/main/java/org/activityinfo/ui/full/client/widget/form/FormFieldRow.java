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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.style.TransitionUtil;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldRow extends Composite {

    private static FormFieldWidgetUiBinder uiBinder = GWT
            .create(FormFieldWidgetUiBinder.class);

    interface FormFieldWidgetUiBinder extends UiBinder<Widget, FormFieldRow> {
    }

    @UiField
    DivElement label;
    @UiField
    DivElement description;
    @UiField
    DivElement unit;
    @UiField
    DivElement control;

    private FormField formField;
    private FormFieldWidget formFieldWidget;

    public FormFieldRow() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormFieldRow(FormField formField) {
        this();
        this.formField = formField;
        this.formFieldWidget = FormFieldWidgetUtil.create(formField);
        render();
    }

    private void render() {
        label.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getLabel().getValue()));
        description.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getDescription().getValue()));
        unit.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getUnit().getValue()));
        control.appendChild(formFieldWidget.getWidget().getElement());
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }
}
