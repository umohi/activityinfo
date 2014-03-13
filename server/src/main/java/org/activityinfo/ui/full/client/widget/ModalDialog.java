package org.activityinfo.ui.full.client.widget;
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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.full.client.style.BaseStylesheet;
import org.activityinfo.ui.full.client.style.ModalStylesheet;
import org.activityinfo.ui.full.client.util.GwtUtil;

/**
 * @author yuriyz on 3/4/14.
 */
public class ModalDialog extends Composite {

    private static ModalDialogBinder uiBinder = GWT
            .create(ModalDialogBinder.class);

    interface ModalDialogBinder extends UiBinder<Widget, ModalDialog> {
    }

    @UiField
    HeadingElement title;
    @UiField
    Button okButton;
    @UiField
    PopupPanel dialog;
    @UiField
    DivElement modalBody;
    @UiField
    DivElement modalFooter;

    public ModalDialog() {
        BaseStylesheet.INSTANCE.ensureInjected();
        ModalStylesheet.INSTANCE.ensureInjected();

        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setVisible(boolean visible) {
        GwtUtil.setVisible(getElement(), visible);
    }

    public void show() {
        dialog.center();
    }

    public void setDialogTitle(String dialogTitle) {
        this.title.setInnerHTML(dialogTitle);
    }

    public Button getOkButton() {
        return okButton;
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        dialog.hide();
    }

    @UiHandler("closeButton")
    public void onClose(ClickEvent event) {
        dialog.hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        dialog.hide();
    }

    public DivElement getModalBody() {
        return modalBody;
    }

    public DivElement getModalFooter() {
        return modalFooter;
    }
}