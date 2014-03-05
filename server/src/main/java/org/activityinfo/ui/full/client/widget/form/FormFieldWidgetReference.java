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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldCardinality;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.dispatch.callback.SuccessCallback;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 2/7/14.
 */
public class FormFieldWidgetReference extends Composite implements FormFieldWidget<Set<Cuid>>, HasInstances {

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

    private final List<ValueChangeHandler<Set<Cuid>>> handlers = Lists.newArrayList();

    @UiField
    FlowPanel panel;

    private ResourceLocator resourceLocator;
    private FormField formField;
    private FormFieldWidget<Set<Cuid>> widget;
    private Set<Cuid> value = Sets.newHashSet();

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
        resourceLocator.queryInstances(ClassCriteria.union(formField.getRange())).then(new SuccessCallback<List<FormInstance>>() {
            @Override
            public void onSuccess(List<FormInstance> result) {
                FormFieldWidgetReference.this.widget = createWidget(result);
                addWidget();
            }
        });
    }

    private void addWidget() {
        if (widget != null) {
            panel.add(widget);
            for (ValueChangeHandler<Set<Cuid>> handler : handlers) {
                widget.addValueChangeHandler(handler);
            }
            widget.setValue(value);
        }
    }

    private FormFieldWidget<Set<Cuid>> createWidget(List<FormInstance> formInstances) {
        final int size = formInstances.size();
        if (this.formField.getCardinality() == FormFieldCardinality.SINGLE) {
            if (size < SMALL_BALANCE_NUMBER) {
                // Radio buttons
                return new FormFieldWidgetReferenceRadioPanel(formInstances);
            } else if (size < MEDIUM_BALANCE_NUMBER) {
                // Dropdown list
                return new FromFieldWidgetReferenceComboBox(formInstances);
            } else {
                // Suggest box
                return new FormFieldWidgetReferenceSuggestBox(formInstances);
            }
        } else {
            if (size < SMALL_BALANCE_NUMBER) {
                // Check boxes
                return new FormFieldWidgetReferenceCheckBoxPanel(formInstances);
            } else if (size < MEDIUM_BALANCE_NUMBER) {
                // List of selected + add button
                return new FormFieldWidgetReferenceListPanel(formInstances);
            } else {
                // List of selected + add button
                return new FormFieldWidgetReferenceListPanel(formInstances);
            }
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (widget != null) {
            widget.setReadOnly(readOnly);
        }
    }

    @Override
    public boolean isReadOnly() {
        return widget != null && widget.isReadOnly();
    }

    @Override
    public Set<Cuid> getValue() {
        if (widget != null) {
            return widget.getValue();
        }
        return Sets.newHashSet();
    }

    @Override
    public void setValue(Set<Cuid> value) {
        if (value != null) {
            if (widget != null) {
                widget.setValue(value);
            } else {
                this.value = value;
            }
        }
    }

    @Override
    public void setValue(Set<Cuid> value, boolean fireEvents) {
        if (widget != null) {
            widget.setValue(value, fireEvents);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Set<Cuid>> handler) {
        if (widget == null) {
            handlers.add(handler);
            return new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    handlers.remove(handler);
                }
            };
        } else {
            return widget.addValueChangeHandler(handler);
        }
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public List<FormInstance> getInstances() {
        if (widget instanceof HasInstances) {
            return ((HasInstances) widget).getInstances();
        }
        return Lists.newArrayList();
    }
}
