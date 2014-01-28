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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormElement;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import java.util.Map;

/**
 * Panel to render UserForm definition.
 *
 * @author YuriyZ
 */
public class UserFormPanel extends Composite {

    private static UserFormPanelUiBinder uiBinder = GWT
            .create(UserFormPanelUiBinder.class);

    interface UserFormPanelUiBinder extends UiBinder<Widget, UserFormPanel> {
    }

    public static final int HORIZONTAL_SPACING = 3;

    private UserForm userForm;
    private FormInstance formInstance;

    private final Button addFieldButton = new Button(I18N.CONSTANTS.newField());
    private final Button removeFieldButton = new Button(I18N.CONSTANTS.removeField());
    private final Map<Iri, FormFieldWidget> controlMap = Maps.newHashMap();

    @UiField
    Button saveButton;
    @UiField
    Button resetButton;
    @UiField
    FlowPanel contentPanel;

    public UserFormPanel() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public UserFormPanel(UserForm userForm, ResourceLocator resourceLocator) {
        this();
        this.userForm = userForm;
        renderForm();
    }

    public void renderForm() {
        for (FormElement element : this.userForm.getElements()) {
            if (element instanceof FormField) {
                final FormFieldWidget w = new FormFieldWidget((FormField) element);
                contentPanel.add(w);
                controlMap.put(element.getId(), w);
            }
        }
    }

    @UiHandler("saveButton")
    public void onSave(ClickEvent event) {
        // todo
    }

    @UiHandler("resetButton")
    public void onReset(ClickEvent event) {
        // todo
    }

    private Widget createToolbar() {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setSpacing(HORIZONTAL_SPACING);
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        addFieldButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //
            }
        });
        removeFieldButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //
            }
        });
        horizontalPanel.add(addFieldButton);
        horizontalPanel.add(removeFieldButton);
        return horizontalPanel;
    }

//    private Widget createContent() {
//        flexTable.setWidth("100%");
//        flexTable.setCellSpacing(3);
//        flexTable.setCellPadding(3);
//
//        final FlexTable.FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
//        cellFormatter.setHorizontalAlignment(0, 4, HasHorizontalAlignment.ALIGN_LEFT);
//        cellFormatter.setColSpan(0, 0, 5);
//        flexTable.setHTML(0, 0, I18N.CONSTANTS.userFormPanelInvitation());
//
//        int row = 1;
//        for (FormElement element : userForm.getElements()) {
//            if (element instanceof FormField) {
//                final FormField field = (FormField) element;
//                flexTable.setWidget(row, 0, new HTML(SafeHtmlUtils.fromString(field.getLabel().getValue())));
//                flexTable.setWidget(row, 1, createWidget(field));
//                flexTable.setWidget(row, 2, new HTML(SafeHtmlUtils.fromString(""))); // unit here
//                final String descriptionHtml = field.getDescription() != null ?
//                        GwtUtil.stringOrEmpty(field.getDescription().getValue()) : "";
//                flexTable.setWidget(row, 3, new HTML(SafeHtmlUtils.fromString(descriptionHtml)));
//                flexTable.setWidget(row, 4, new HTML("")); // buttons here
//                row++;
//            }
//        }
//
//        return flexTable;
//    }

    public UserForm getUserForm() {
        return userForm;
    }

    public FormInstance getFormInstance() {
        return null;
    }

    public void setDesignEnabled(boolean designEnabled) {

    }

    public void setValue(FormInstance formInstance) {
        this.formInstance = formInstance;
    }

    public void setReadOnly(boolean readOnly) {

    }
}
