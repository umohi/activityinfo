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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.local.command.handler.KeyGenerator;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.List;

/**
 * @author yuriyz on 2/7/14.
 */
public class FormFieldWidgetReferenceRadioPanel extends Composite implements FormFieldWidget<Iri> {

    private static FormFieldWidgetReferenceRadioPanelUiBinder uiBinder = GWT
            .create(FormFieldWidgetReferenceRadioPanelUiBinder.class);

    interface FormFieldWidgetReferenceRadioPanelUiBinder extends UiBinder<Widget, FormFieldWidgetReferenceRadioPanel> {
    }

    @UiField
    FlowPanel panel;

    private List<FormInstance> items;


    public FormFieldWidgetReferenceRadioPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void init(List<FormInstance> formInstances) {
        final String groupId = Integer.toString(new KeyGenerator().generateInt());
        for (FormInstance formInstance : formInstances) {
            final RadioButton radioButton = new RadioButton(groupId, formInstance.getId().asString());
            panel.add(radioButton);
        }
    }

    public List<FormInstance> getItems() {
        return items;
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Iri getValue() {
        return null;
    }

    @Override
    public void setValue(Iri value) {

    }

    @Override
    public void setValue(Iri value, boolean fireEvents) {

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Iri> handler) {
        return null;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {

    }

    @Override
    public Widget asWidget() {
        return null;
    }
}
