package org.activityinfo.ui.full.client.component.form;
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
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api.client.KeyGenerator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormInstanceLabeler;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.widget.RadioButton;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 2/7/14.
 */
public class FormFieldWidgetReferenceRadioPanel extends Composite implements FormFieldWidget<Set<Cuid>>, HasInstances {

    private static FormFieldWidgetReferenceRadioPanelUiBinder uiBinder = GWT
            .create(FormFieldWidgetReferenceRadioPanelUiBinder.class);

    interface FormFieldWidgetReferenceRadioPanelUiBinder extends UiBinder<Widget, FormFieldWidgetReferenceRadioPanel> {
    }

    @UiField
    VerticalPanel panel;

    private final Map<Cuid, RadioButton> controls = Maps.newHashMap();
    private List<FormInstance> instances;

    public FormFieldWidgetReferenceRadioPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormFieldWidgetReferenceRadioPanel(List<FormInstance> formInstances) {
        this();
        init(formInstances);
    }

    public void init(List<FormInstance> instances) {
        this.instances = instances;
        final String groupId = Integer.toString(new KeyGenerator().generateInt());
        for (final FormInstance formInstance : instances) {
            final RadioButton radioButton = new RadioButton(groupId, FormInstanceLabeler.getLabel(formInstance));
            radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    fireEvent(new CuidValueChangeEvent(Sets.newHashSet(formInstance.getId())));
                }
            });

            panel.add(radioButton);
            controls.put(formInstance.getId(), radioButton);
        }
    }

    public List<FormInstance> getInstances() {
        return instances;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (RadioButton radioButton : controls.values()) {
            radioButton.setEnabled(!readOnly);
        }
    }

    @Override
    public boolean isReadOnly() {
        return !controls.isEmpty() && !controls.values().iterator().next().isEnabled();
    }

    @Override
    public Set<Cuid> getValue() {
        for (Map.Entry<Cuid, RadioButton> entry : controls.entrySet()) {
            if (entry.getValue().getValue()) {
                return Sets.newHashSet(entry.getKey());
            }
        }
        return Sets.newHashSet();
    }

    @Override
    public void setValue(Set<Cuid> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<Cuid> value, boolean fireEvents) {
        final Set<Cuid> oldValue = getValue();
        if (value != null && !value.isEmpty()) {
            final RadioButton radioButton = controls.get(value.iterator().next());
            if (radioButton != null) {
                radioButton.setValue(true);
            }
        } else {
            for (Map.Entry<Cuid, RadioButton> entry : controls.entrySet()) {
                entry.getValue().setValue(false);
            }
        }
        if (fireEvents) {
            CuidValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<Cuid>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
