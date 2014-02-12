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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

    @UiField
    ListBox selectedList;
    @UiField(provided = true)
    SuggestBox suggestBox;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    //    private final Map<Integer, Cuid> listIndexToCuidMap = Maps.newHashMap();
    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    private List<FormInstance> instances;

    public FormFieldWidgetReferenceListPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        suggestBox = new SuggestBox(oracle);
    }

    public FormFieldWidgetReferenceListPanel(List<FormInstance> formInstances) {
        this();
        init(formInstances);
    }

    public void init(List<FormInstance> instances) {
        this.instances = instances;
        initOracle(instances);
    }

    @UiHandler("addButton")
    public void onAdd(ClickEvent event) {
        // todo
        fireEvent(new CuidValueChangeEvent(Sets.newHashSet(getValue())));
    }

    @UiHandler("removeButton")
    public void onRemove(ClickEvent event) {
        // todo
        fireEvent(new CuidValueChangeEvent(Sets.newHashSet(getValue())));
    }

    private void initOracle(List<FormInstance> instances) {
        for (FormInstance instance : instances) {
            final String labelValue = FormInstanceLabeler.getLabel(instance);
            oracle.add(labelValue);
            labelToCuidMap.put(labelValue, instance.getId());
        }
    }

    public List<FormInstance> getInstances() {
        return instances;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        final boolean enabled = !readOnly;
        selectedList.setEnabled(enabled);
        suggestBox.setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
    }

    @Override
    public boolean isReadOnly() {
        return !selectedList.isEnabled();
    }

    @Override
    public Set<Cuid> getValue() {
        final Set<Cuid> value = Sets.newHashSet();
        // todo : resolve index from list
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
            // todo : resolve index from list

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
