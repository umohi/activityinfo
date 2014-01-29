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

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormField;

/**
 * @author yuriyz on 1/28/14.
 */
public class SimpleFormFieldWidget implements FormFieldWidget<Widget> {

    private final Widget widget;
    private final FormField formField;

    public SimpleFormFieldWidget(FormField formField) {
        this(new Label(), formField);
    }

    public SimpleFormFieldWidget(Widget widget, FormField formField) {
        Preconditions.checkNotNull(widget);
        Preconditions.checkNotNull(formField);
        this.widget = widget;
        this.formField = formField;
    }

    public Widget getControl() {
        return widget;
    }

    public Widget getWidget() {
        return widget;
    }

    public FormField getFormField() {
        return formField;
    }
}
