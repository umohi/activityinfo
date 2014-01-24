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

import com.google.gwt.user.client.ui.ResizeComposite;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.UserForm;

/**
 * Panel to render UserForm definition.
 *
 * @author YuriyZ
 */
public class UserFormPanel extends ResizeComposite {

    private final UserForm userForm;

    public UserFormPanel(UserForm p_userForm) {
        this.userForm = p_userForm;
    }

    public UserForm getUserForm() {
        return userForm;
    }

    public FormInstance getFormInstance() {
        return null; // todo
    }

    public void setDesignEnabled(boolean p_designEnabled) {

    }

    public void setValue(FormInstance p_instance) {

    }

    public void setReadOnly(boolean p_readOnly) {

    }
}
