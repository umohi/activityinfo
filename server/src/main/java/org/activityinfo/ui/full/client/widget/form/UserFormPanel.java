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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.ui.full.client.i18n.I18N;

/**
 * Panel to render UserForm definition.
 *
 * @author YuriyZ
 */
public class UserFormPanel extends DockLayoutPanel {

    private final UserForm userForm;
    private final ResourceLocator resourceLocator;
    private FormInstance formInstance;

    // toolbar widgets
    private final Button addFieldButton = new Button(I18N.CONSTANTS.newField());
    private final Button removeFieldButton = new Button(I18N.CONSTANTS.removeField());

    // footer widgets
    private final Button saveButton = new Button(I18N.CONSTANTS.save());
    private final Button resetButton = new Button(I18N.CONSTANTS.reset());

    // content

    public UserFormPanel(UserForm userForm, ResourceLocator resourceLocator) {
        super(Style.Unit.EM);
        this.userForm = userForm;
        this.resourceLocator = resourceLocator;
        init();
    }

    private void init() {
        addNorth(createToolbar(), 2);
        addSouth(createFooter(), 2);
        addWest(new HTML("navigation"), 10);
        add(createContent());
    }

    private Widget createFooter() {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setSpacing(3);
        horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //
            }
        });
        resetButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                //
            }
        });
        horizontalPanel.add(saveButton);
        horizontalPanel.add(resetButton);
        return horizontalPanel;
    }

    private Widget createToolbar() {
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setSpacing(3);
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

    private Widget createContent() {
        return new HTML("content");
    }


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
