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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.dialog.DialogActionType;

/**
 * @author yuriyz on 2/18/14.
 */
public class FormSectionRow extends Composite {

    private static FormSectionRowUiBinder uiBinder = GWT
            .create(FormSectionRowUiBinder.class);

    interface FormSectionRowUiBinder extends UiBinder<Widget, FormSectionRow> {
    }

    private final FormSection formSection;
    private final FormPanel formPanel;
    private final ElementNode node;
    private final ElementNode parentNode;

    @UiField
    HTML label;
    @UiField
    RowToolbar toolbar;
    @UiField
    FlowPanel contentPanel;

    public FormSectionRow(FormSection formSection, FormPanel formPanel, ElementNode parentNode) {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        this.formSection = formSection;
        this.formPanel = formPanel;
        this.toolbar.attach(this);
        this.toolbar.setFormPanel(formPanel);
        this.label.setHTML(SafeHtmlUtils.fromSafeConstant(formSection.getLabel().getValue()));
        this.node = new ElementNode(formPanel, contentPanel, parentNode, formSection);
        this.node.renderElements(formSection.getElements());
        this.parentNode = parentNode;
        setLabelText();
        addHandlers();
    }

    public void putFormFieldRows(BiMap<Cuid, FormFieldRow> ownAndChildFieldMap) {
        node.putFormFieldRows(ownAndChildFieldMap);
    }

    public void setLabelText() {
        this.label.setHTML(SafeHtmlUtils.fromSafeConstant(formSection.getLabel().getValue()));
    }

    public ElementNode getNode() {
        return node;
    }

    public FormSection getFormSection() {
        return formSection;
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    private void addHandlers() {
        toolbar.getEditButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                edit(DialogActionType.EDIT);
            }
        });
        toolbar.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                edit(DialogActionType.ADD);
            }
        });
        toolbar.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parentNode.remove(FormSectionRow.this);
            }
        });
        toolbar.getUpButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parentNode.moveUpWidget(FormSectionRow.this, formSection, true);
            }
        });
        toolbar.getDownButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parentNode.moveDownWidget(FormSectionRow.this, formSection, true);
            }
        });
    }

    private void edit(DialogActionType actionType) {
        final FormSectionEditDialog dialog = new FormSectionEditDialog(this, actionType);
        dialog.show();
    }

}
