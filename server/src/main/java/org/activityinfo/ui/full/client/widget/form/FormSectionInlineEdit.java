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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.CompositeWithMirror;

/**
 * @author yuriyz on 2/25/14.
 */
public class FormSectionInlineEdit extends CompositeWithMirror {
    private static FormSectionInlineEditBinder uiBinder = GWT
            .create(FormSectionInlineEditBinder.class);

    interface FormSectionInlineEditBinder extends UiBinder<Widget, FormSectionInlineEdit> {
    }

    private FormSection formSection;

    @UiField
    Button okButton;
    @UiField
    TextBox sectionLabel;

    public FormSectionInlineEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void applyNew(Element... mirrorElements) {
        final Cuid newCuid = CuidAdapter.newSectionField();
        final FormSection newSection = new FormSection(newCuid);
        apply(newSection, mirrorElements);
    }

    public void apply() {
        this.sectionLabel.setValue(formSection != null ? formSection.getLabel().getValue() : "");
        this.sectionLabel.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                setOkButtonState();
            }
        });
        setOkButtonState();
    }

    public void apply(FormSection formSection, Element... mirrorElements) {
        setFormSection(formSection);
        setMirrorElements(mirrorElements);
        apply();
    }

    public void setFormSection(FormSection formSection) {
        this.formSection = formSection;
    }

    public FormSection getFormSection() {
        return formSection;
    }

    private void setOkButtonState() {
        okButton.setEnabled(formSection != null && !formSection.getLabel().getValue().equals(sectionLabel.getValue()));
    }

    public Button getOkButton() {
        return okButton;
    }

    public TextBox getSectionLabel() {
        return sectionLabel;
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
}
