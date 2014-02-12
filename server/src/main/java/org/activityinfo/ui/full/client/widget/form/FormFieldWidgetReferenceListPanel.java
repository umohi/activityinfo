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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormInstanceLabeler;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 2/11/14.
 */
public class FormFieldWidgetReferenceListPanel extends Composite implements FormFieldWidget<Set<Cuid>> {

    private static FormFieldWidgetReferenceListPanelUiBinder uiBinder = GWT
            .create(FormFieldWidgetReferenceListPanelUiBinder.class);

    interface FormFieldWidgetReferenceListPanelUiBinder extends UiBinder<Widget, FormFieldWidgetReferenceListPanel> {
    }

    private final Map<String, Cuid> labelToCuidMap = Maps.newHashMap();

    private ListBox dropBox;
    private SuggestBox suggestBox;

    @UiField
    HorizontalPanel panel;

    private final Map<Cuid, CheckBox> controls = Maps.newHashMap();
    private List<FormInstance> instances;

    public FormFieldWidgetReferenceListPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public FormFieldWidgetReferenceListPanel(List<FormInstance> formInstances) {
        this();
        init(formInstances);
    }

    public void init(List<FormInstance> instances) {
        this.instances = instances;
        this.dropBox = new ListBox(true);
        this.suggestBox = createSuggestBox(instances);

        for (FormInstance instance : instances) {
            dropBox.addItem(FormInstanceLabeler.getLabel(instance), instance.getId().asString());
        }
        dropBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireEvent(new CuidValueChangeEvent(getValue()));
            }
        });
    }

    private SuggestBox createSuggestBox(List<FormInstance> instances) {
        final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        for (FormInstance instance : instances) {
            final String labelValue = FormInstanceLabeler.getLabel(instance);
            oracle.add(labelValue);
            labelToCuidMap.put(labelValue, instance.getId());
        }
        final SuggestBox box = new SuggestBox(oracle);
        box.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fireEvent(new CuidValueChangeEvent(Sets.newHashSet(getValue())));
            }
        });
        return box;
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
        if (value != null && !value.isEmpty()) {
            final Set<Cuid> oldValue = getValue();
            for (Map.Entry<Cuid, CheckBox> entry : controls.entrySet()) {
                entry.getValue().setValue(value.contains(entry.getKey()));
            }

            if (fireEvents) {
                CuidValueChangeEvent.fireIfNotEqual(this, oldValue, value);
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<Cuid>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
