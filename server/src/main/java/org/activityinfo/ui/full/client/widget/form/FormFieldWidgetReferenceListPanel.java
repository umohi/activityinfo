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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import org.activityinfo.ui.full.client.widget.undo.UndoManager;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 2/11/14.
 */
public class FormFieldWidgetReferenceListPanel extends Composite implements FormFieldWidget<Set<Cuid>> {

    private static FormFieldWidgetReferenceListPanelUiBinder uiBinder = GWT
            .create(FormFieldWidgetReferenceListPanelUiBinder.class);

    interface FormFieldWidgetReferenceListPanelUiBinder extends UiBinder<Widget, FormFieldWidgetReferenceListPanel> {
    }

    @UiField
    ListBox selectedList;
    @UiField(provided = true)
    SuggestBox suggestBox;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    private final BiMap<String, Cuid> labelToCuidBiMap = HashBiMap.create();
    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    private List<FormInstance> instances;

    public FormFieldWidgetReferenceListPanel(UndoManager undoManager) {
        suggestBox = FormFieldWidgetFactory.createSuggestBox(oracle, undoManager);
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        setRemoveButtonState();
    }

    public FormFieldWidgetReferenceListPanel(UndoManager undoManager, List<FormInstance> formInstances) {
        this(undoManager);
        init(formInstances);
    }

    public void init(List<FormInstance> instances) {
        this.instances = instances;
        initOracle(instances);
        selectedList.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setRemoveButtonState();
            }
        });
    }

    private void setRemoveButtonState() {
        removeButton.setEnabled(selectedList.getSelectedIndex() != -1);
    }

    @UiHandler("addButton")
    public void onAdd(ClickEvent event) {
        final String label = suggestBox.getValue();
        final Cuid cuid = labelToCuidBiMap.get(label);
        if (cuid != null && !getValue().contains(cuid)) {
            selectedList.addItem(label, cuid.asString());
            suggestBox.setValue(""); // clear box
            fireEvent(new CuidValueChangeEvent(Sets.newHashSet(getValue())));
        }
    }

    @UiHandler("removeButton")
    public void onRemove(ClickEvent event) {
        for (int i = 0; i < selectedList.getItemCount(); i++) {
            if (selectedList.isItemSelected(i)) {
                selectedList.removeItem(i);
            }
        }
        fireEvent(new CuidValueChangeEvent(Sets.newHashSet(getValue())));
    }

    private void initOracle(List<FormInstance> instances) {
        for (FormInstance instance : instances) {
            final String labelValue = FormInstanceLabeler.getLabel(instance);
            oracle.add(labelValue);
            labelToCuidBiMap.forcePut(labelValue, instance.getId());
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
        for (int i = 0; i < selectedList.getItemCount(); i++) {
            value.add(new Cuid(selectedList.getValue(i)));
        }
        return value;
    }

    @Override
    public void setValue(Set<Cuid> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Set<Cuid> value, boolean fireEvents) {
        selectedList.clear();
        final Set<Cuid> oldValue = getValue();
        if (value != null && !value.isEmpty()) {
            for (Cuid cuid : value) {
                final String label = labelToCuidBiMap.inverse().get(cuid);
                selectedList.addItem(label, cuid.asString());
            }
        } else {
            suggestBox.setValue("");
            selectedList.clear();
            setRemoveButtonState();
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
