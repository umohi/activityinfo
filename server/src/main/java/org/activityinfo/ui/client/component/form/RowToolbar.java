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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.util.GwtUtil;

/**
 * @author yuriyz on 2/19/14.
 */
public class RowToolbar extends Composite {

    private static RowToolbarUiBinder uiBinder = GWT
            .create(RowToolbarUiBinder.class);

    interface RowToolbarUiBinder extends UiBinder<Widget, RowToolbar> {
    }

    @UiField
    Button editButton;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    Button upButton;
    @UiField
    Button downButton;
    @UiField
    DivElement toolbar;
    @UiField
    Button addSectionButton;

    private FormPanel formPanel;

    public RowToolbar() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Attaches toolbar to widget.
     *
     * @param widget widget to which toolbar is attached
     */
    public void attach(Widget widget) {
        widget.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (isDesignEnabled()) {
                    GwtUtil.setVisible(true, toolbar);
                }
            }
        }, MouseOverEvent.getType());
        widget.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (isDesignEnabled()) {
                    GwtUtil.setVisible(false, toolbar);
                }
            }
        }, MouseOutEvent.getType());
    }

    public Button getAddSectionButton() {
        return addSectionButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }

    public boolean isDesignEnabled() {
        return formPanel != null && formPanel.isDesignEnabled();
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    public void setFormPanel(FormPanel formPanel) {
        this.formPanel = formPanel;
    }
}