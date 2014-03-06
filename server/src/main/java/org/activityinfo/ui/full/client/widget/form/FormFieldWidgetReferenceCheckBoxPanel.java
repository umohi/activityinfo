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
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormInstanceLabeler;
import org.activityinfo.api2.shared.form.has.HasInstances;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 2/11/14.
 */
public class FormFieldWidgetReferenceCheckBoxPanel extends Composite implements FormFieldWidget<Set<Cuid>>, HasInstances {

    private static FromFieldWidgetReferenceCheckBoxPanelUiBinder uiBinder = GWT
            .create(FromFieldWidgetReferenceCheckBoxPanelUiBinder.class);

    interface FromFieldWidgetReferenceCheckBoxPanelUiBinder extends UiBinder<Widget, FormFieldWidgetReferenceCheckBoxPanel> {
    }

    @UiField
    VerticalPanel panel;

    private final Map<Cuid, CheckBox> controls = Maps.newHashMap();
    private List<FormInstance> instances;

    public FormFieldWidgetReferenceCheckBoxPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormFieldWidgetReferenceCheckBoxPanel(List<FormInstance> formInstances) {
        this();
        init(formInstances);
    }

    public void init(List<FormInstance> instances) {
        this.instances = instances;
        for (final FormInstance formInstance : instances) {
            final CheckBox checkBox = new CheckBox(FormInstanceLabeler.getLabel(formInstance), false);
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    fireEvent(new CuidValueChangeEvent(getValue()));
                }
            });

            panel.add(checkBox);
            controls.put(formInstance.getId(), checkBox);
        }
    }

    public List<FormInstance> getInstances() {
        return instances;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (CheckBox checkBox : controls.values()) {
            checkBox.setEnabled(!readOnly);
        }
    }

    @Override
    public boolean isReadOnly() {
        return !controls.isEmpty() && !controls.values().iterator().next().isEnabled();
    }

    @Override
    public Set<Cuid> getValue() {
        final Set<Cuid> value = Sets.newHashSet();
        for (Map.Entry<Cuid, CheckBox> entry : controls.entrySet()) {
            if (entry.getValue().getValue()) {
                value.add(entry.getKey());
            }
        }
        return value;
    }

    @Override
    public void setValue(Set<Cuid> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<Cuid> value, boolean fireEvents) {
        final Set<Cuid> oldValue = getValue();
        for (Map.Entry<Cuid, CheckBox> entry : controls.entrySet()) {
            entry.getValue().setValue(value != null && value.contains(entry.getKey()));
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
