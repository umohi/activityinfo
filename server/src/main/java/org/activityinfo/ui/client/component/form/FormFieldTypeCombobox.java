package org.activityinfo.ui.client.component.form;
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
import com.google.gwt.user.client.ui.ListBox;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.ui.client.i18n.FromEntities;

import java.util.Set;

/**
 * @author yuriyz on 3/4/14.
 */
public class FormFieldTypeCombobox extends ListBox {

    private final BiMap<Integer, FormFieldType> typeIndexMap = HashBiMap.create();
    private Set<FormFieldType> types;

    public FormFieldTypeCombobox() {
        this(false);
    }

    public FormFieldTypeCombobox(boolean isMultipleSelect) {
        super(isMultipleSelect);
        setTypes(Sets.newHashSet(FormFieldType.values()));
        addStyleName("form-control");
    }

    public void setTypes(Set<FormFieldType> types) {
        this.types = types;
        clear();
        typeIndexMap.clear();
        int index = 0;
        for (FormFieldType fieldType : types) {
            insertItem(FromEntities.INSTANCE.getFormFieldType(fieldType), fieldType.name(), index);
            typeIndexMap.put(index, fieldType);
            index++;
        }
    }

    public Set<FormFieldType> getTypes() {
        return this.types;
    }

    public boolean hasTypes() {
        return this.types != null && !this.types.isEmpty();
    }

    public FormFieldType getSelectedType() {
        return typeIndexMap.get(getSelectedIndex());
    }

    private int getIndexByType(FormFieldType type) {
        return typeIndexMap.inverse().get(type);
    }

    public void setSelectedType(FormFieldType type) {
        setSelectedIndex(getIndexByType(type));
    }

}
