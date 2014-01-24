package org.activityinfo.dev.client;
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

import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormElement;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.ui.full.client.widget.form.UserFormPanel;

/**
 * @author yuriyz on 1/24/14.
 */
public class UserFormPanelTest {

    /**
     * Avoid instance creation
     */
    private UserFormPanelTest() {
    }

    public static Widget test() {
        final UserForm userForm = createTestUserForm();
        final UserFormPanel p = new UserFormPanel(userForm);
        p.setDesignEnabled(true);
        return p;
    }

    private static UserForm createTestUserForm() {
        final FormElement e1 = new FormField(DevUtils.randomIri());

        final UserForm form = new UserForm(DevUtils.randomIri());
        form.addElement(e1);
        return form;
    }
}
