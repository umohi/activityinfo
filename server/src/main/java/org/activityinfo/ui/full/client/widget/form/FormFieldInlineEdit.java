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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.CompositeWithMirror;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 2/26/14.
 */
public class FormFieldInlineEdit extends CompositeWithMirror {

    private static FormFieldInlineEditBinder uiBinder = GWT
            .create(FormFieldInlineEditBinder.class);

    interface FormFieldInlineEditBinder extends UiBinder<Widget, FormFieldInlineEdit> {
    }

    private final BiMap<Integer,FormFieldType> typeIndexMap = HashBiMap.create();
    private FormField formField;

    @UiField
    TextBox label;
    @UiField
    TextArea description;
    @UiField
    TextBox unit;
    @UiField
    ListBox type;

    public FormFieldInlineEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
        initTypeControl();
    }

    private void initTypeControl() {
        int index = 0;
        for (FormFieldType fieldType : FormFieldType.values()) {
            type.insertItem(I18N.FROM_ENTITIES.getFormFieldType(fieldType), fieldType.name(), index);
            typeIndexMap.put(index, fieldType);
            index++;
        }
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        hide();
    }

    public void hide() {
        setVisible(false);
    }

    public void applyNew(Element... mirrorElements) {
        final Cuid newCuid = CuidAdapter.newFormField();
        final FormField newFormField = new FormField(newCuid);
        newFormField.setType(FormFieldType.FREE_TEXT);
        apply(newFormField, mirrorElements);
    }

    public void apply(@Nonnull FormField formField, Element... mirrorElements) {
        setFormField(formField);
        setMirrorElements(mirrorElements);
        apply();
    }

    private void apply() {
        label.setValue(formField.getLabel().getValue());
        description.setValue(formField.getLabel().getValue());
        unit.setValue(formField.getUnit().getValue());
        type.setSelectedIndex(typeIndexMap.inverse().get(formField.getType()));
    }

    public void updateModel() {
        // todo
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }
}
