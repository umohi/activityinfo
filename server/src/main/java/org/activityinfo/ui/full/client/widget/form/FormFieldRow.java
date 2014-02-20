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

import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.HasReadOnly;

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
    @UiField
    RowToolbar toolbar;

    private FormPanel formPanel;
    private FormField formField;
    private IsWidget formFieldWidget;
    private ElementNode node;

    public FormFieldRow(FormField formField, FormPanel formPanel, ElementNode node) {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        this.formPanel = formPanel;
        this.formField = formField;
        this.node = node;
        this.formFieldWidget = FormFieldWidgetFactory.create(formField, formPanel);
        this.toolbar.attach(this);
        this.toolbar.setFormPanel(formPanel);

        addHandlers();
        render();
    }

    private void render() {
        label.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getLabel().getValue()));
        description.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getDescription().getValue()));
        unit.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getUnit().getValue()));
        control.add(formFieldWidget);
    }

    private void addHandlers() {
        toolbar.getEditButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // todo
            }
        });
        toolbar.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // todo
            }
        });
        toolbar.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.remove(FormFieldRow.this);
            }
        });
        toolbar.getUpButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveUpWidget(FormFieldRow.this, formField, true);
            }
        });
        toolbar.getDownButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveDownWidget(FormFieldRow.this, formField, true);
            }
        });
    }


    public void setValue(Object value) {
        if (value instanceof Cuid && formFieldWidget instanceof FormFieldWidgetReference) { // autofix of wrong data in form instance
            ((FormFieldWidgetReference) formFieldWidget).setValue(Sets.newHashSet((Cuid) value));
        } else if (formFieldWidget instanceof HasValue) { // run here is data in form instance is correct
            ((HasValue) formFieldWidget).setValue(value);
        }
    }

    public Object setValue() {
        if (formFieldWidget instanceof HasValue) {
            return ((HasValue) formFieldWidget).getValue();
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

    public IsWidget getFormFieldWidget() {
        return formFieldWidget;
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
