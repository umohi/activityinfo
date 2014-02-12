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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormInstanceLabeler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 2/10/14.
 */
public class FromFieldWidgetReferenceComboBox extends Composite implements FormFieldWidget<Set<Cuid>> {

    private final ListBox dropBox = new ListBox(false);

    private final Map<Integer, Cuid> dropdownIndexToCuidMap = Maps.newHashMap();

    public FromFieldWidgetReferenceComboBox(List<FormInstance> instances) {
        initWidget(dropBox);
        for (FormInstance instance : instances) {
            dropBox.addItem(FormInstanceLabeler.getLabel(instance), instance.getId().asString());
        }
        for (int i = 0; i < dropBox.getItemCount(); i++) {
            dropdownIndexToCuidMap.put(i, new Cuid(dropBox.getValue(i)));
        }
        dropBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireEvent(new CuidValueChangeEvent(getValue()));
            }
        });
    }

    public int getIndexByCuid(Cuid cuid) {
        for (Map.Entry<Integer, Cuid> entry : dropdownIndexToCuidMap.entrySet()) {
            if (entry.getValue().equals(cuid)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        dropBox.setEnabled(!readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return !dropBox.isEnabled();
    }

    @Override
    public Set<Cuid> getValue() {
        final int selectedIndex = dropBox.getSelectedIndex();
        if (selectedIndex != -1) {
            final String cuidAsString = dropBox.getValue(selectedIndex);
            return Sets.newHashSet(new Cuid(cuidAsString));
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
            dropBox.setSelectedIndex(getIndexByCuid(value.iterator().next()));
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
