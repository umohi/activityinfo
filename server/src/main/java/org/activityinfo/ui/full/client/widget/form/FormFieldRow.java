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
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.HasReadOnly;

import java.io.Serializable;

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
    FlowPanel control;

    private FormField formField;
    private IsWidget formFieldWidget;

    public FormFieldRow() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormFieldRow(FormField formField) {
        this();
        this.formField = formField;
        this.formFieldWidget = FormFieldWidgetFactory.create(formField);
        render();
    }

    private void render() {
        label.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getLabel().getValue()));
        description.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getDescription().getValue()));
        unit.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getUnit().getValue()));
        control.add(formFieldWidget);
    }

    public void setValue(Object value) {
        if (formFieldWidget instanceof HasValue) {
            ((HasValue) formFieldWidget).setValue(value);
        }
    }

    public Serializable setValue() {
        if (formFieldWidget instanceof HasValue) {
            return ((HasValue<Serializable>) formFieldWidget).getValue();
        }
        return null;
    }

    public void setReadOnly(boolean readOnly) {
        if (formFieldWidget instanceof ValueBoxBase) {
            ((ValueBoxBase) formFieldWidget).setReadOnly(readOnly);
        } else if (formFieldWidget instanceof HasReadOnly) {
            ((HasReadOnly) formFieldWidget).setReadOnly(readOnly);
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
        }
    }

    public boolean isReadOnly() {
        if (formFieldWidget instanceof ValueBoxBase) {
            return ((ValueBoxBase) formFieldWidget).isReadOnly();
        } else if (formFieldWidget instanceof HasReadOnly) {
            return ((HasReadOnly) formFieldWidget).isReadOnly();
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
            return false;
        }
    }

    public void clear() {
        setValue(null);
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }
}
