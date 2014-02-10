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
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.criteria.InstanceCriteria;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldCardinality;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.dispatch.callback.SuccessCallback;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;

/**
 * @author yuriyz on 2/7/14.
 */
public class FormFieldWidgetReference extends Composite implements FormFieldWidget<Iri> {

    /**
     * Based on this numbers FormField Widget generates different widgets and layouts:
     * <p/>
     * 1. Single :
     * less SMALL_BALANCE_NUMBER -> Radio buttons
     * less MEDIUM_BALANCE_NUMBER -> Dropdown list
     * more MEDIUM_BALANCE_NUMBER -> Suggest box
     * 2. Multiple :
     * less SMALL_BALANCE_NUMBER -> Check boxes
     * less MEDIUM_BALANCE_NUMBER -> List of selected + add button
     * more MEDIUM_BALANCE_NUMBER -> List of selected + add button
     */
    public static final int SMALL_BALANCE_NUMBER = 10;
    public static final int MEDIUM_BALANCE_NUMBER = 20;

    private static FormFieldWidgetReferenceUiBinder uiBinder = GWT
            .create(FormFieldWidgetReferenceUiBinder.class);

    interface FormFieldWidgetReferenceUiBinder extends UiBinder<Widget, FormFieldWidgetReference> {
    }

    private ResourceLocator resourceLocator;
    @UiField
    FlowPanel panel;

    private FormField formField;
    private FormFieldWidget<Iri> widget;

    public FormFieldWidgetReference(final FormField formField, final ResourceLocator resourceLocator) {
        this.formField = formField;
        this.resourceLocator = resourceLocator;
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        loadFormInstances(formField);
    }

    public FlowPanel getPanel() {
        return panel;
    }

    private void loadFormInstances(final FormField formField) {
        resourceLocator.queryInstances(InstanceCriteria.getInstance(formField.getRange())).then(new SuccessCallback<List<FormInstance>>() {
            @Override
            public void onSuccess(List<FormInstance> result) {
                FormFieldWidgetReference.this.widget = createWidget(result);
                if (FormFieldWidgetReference.this.widget != null) {
                    FormFieldWidgetReference.this.panel.add(FormFieldWidgetReference.this.widget);
                }
            }
        });
    }

    private FormFieldWidget<Iri> createWidget(List<FormInstance> formInstances) {
        final int size = formInstances.size();
        if (this.formField.getCardinality() == FormFieldCardinality.SINGLE) {
            if (size < SMALL_BALANCE_NUMBER) {
                // Radio buttons
                final FormFieldWidgetReferenceRadioPanel radioPanel = new FormFieldWidgetReferenceRadioPanel();
                radioPanel.init(formInstances);
            } else if (size < MEDIUM_BALANCE_NUMBER) {
                // Dropdown list
                final ListBox dropBox = new ListBox(false);
                for (FormInstance instance : formInstances) {
                    //dropBox.addItem(instance.g, instance.getId().asString());
                }

            } else {
                // Suggest box
            }
        } else {
            if (size < SMALL_BALANCE_NUMBER) {
                // Check boxes
            } else if (size < MEDIUM_BALANCE_NUMBER) {
                // List of selected + add button
                final ListBox dropBox = new ListBox(true);
            } else {
                // List of selected + add button
            }
        }
        return null;
    }

    public FormField getFormField() {
        return formField;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (widget != null) {
            widget.setReadOnly(readOnly);
        }
    }

    @Override
    public boolean isReadOnly() {
        if (widget != null) {
            return widget.isReadOnly();
        }
        return false;
    }

    @Override
    public Iri getValue() {
        if (widget != null) {
            return widget.getValue();
        }
        return null;
    }

    @Override
    public void setValue(Iri value) {
        if (widget != null) {
            widget.setValue(value);
        }
    }

    @Override
    public void setValue(Iri value, boolean fireEvents) {
        if (widget != null) {
            widget.setValue(value, fireEvents);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Iri> handler) {
        if (widget != null) {
            return widget.addValueChangeHandler(handler);
        }
        return this.addValueChangeHandler(handler);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
