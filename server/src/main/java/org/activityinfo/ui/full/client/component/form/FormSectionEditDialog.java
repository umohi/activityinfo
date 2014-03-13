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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.util.GwtUtil;
import org.activityinfo.ui.full.client.component.form.dialog.ActionType;

import javax.annotation.Nonnull;

/**
 * @author yuriyz on 2/20/14.
 */
public class FormSectionEditDialog extends Composite {

    private static FormSectionEditDialogBinder uiBinder = GWT
            .create(FormSectionEditDialogBinder.class);

    interface FormSectionEditDialogBinder extends UiBinder<Widget, FormSectionEditDialog> {
    }

    private final FormSection formSection;
    private final ActionType actionType;

    @UiField
    HeadingElement title;
    @UiField
    Button okButton;
    @UiField
    TextBox sectionLabel;
    @UiField
    PopupPanel dialog;

    public FormSectionEditDialog(FormSection formSection, @Nonnull ActionType actionType) {
        initWidget(uiBinder.createAndBindUi(this));

        this.formSection = formSection;
        this.actionType = actionType;
        this.title.setInnerHTML(getDialogTitle());

        this.sectionLabel.setValue(formSection != null && actionType == ActionType.EDIT ?
                formSection.getLabel().getValue() : "");
        this.sectionLabel.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                setOkButtonState();
            }
        });
        setOkButtonState();
    }

    private void setOkButtonState() {
        okButton.setEnabled(formSection != null && !formSection.getLabel().getValue().equals(sectionLabel.getValue()));
    }

    public void setVisible(boolean visible) {
        GwtUtil.setVisible(getElement(), visible);
    }

    public void show() {
        dialog.center();
    }

    private String getDialogTitle() {
        switch (actionType) {
            case ADD:
                return I18N.CONSTANTS.addSection();
            case EDIT:
                return I18N.CONSTANTS.editSection();
        }
        return "";
    }

    public Button getOkButton() {
        return okButton;
    }

    public TextBox getSectionLabel() {
        return sectionLabel;
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
}
