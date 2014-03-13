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

import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.util.GwtUtil;
import org.activityinfo.ui.full.client.widget.DateBoxWithReadOnly;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldWidgetFactory {

    private FormFieldWidgetFactory() {
    }

    public static IsWidget create(FormField field, FormPanel formPanel) {
        final FormFieldType fieldType = field.getType();
        if (fieldType != null) {
            switch (fieldType) {
                case QUANTITY:
                    return createDoubleBox();
                case NARRATIVE:
                    return createTextArea();
                case FREE_TEXT:
                    return createTextBox();
                case LOCAL_DATE:
                    return createDateTextBox();
                case GEOGRAPHIC_POINT:
                    return new GeographicTextBox();
                case REFERENCE:
                    return new FormFieldWidgetReference(field, formPanel.getResourceLocator());
                default:
                    Log.error("Field type " + fieldType + " is not supported, created text box widget as fallback.");
            }
        }
        return new FormFieldWidgetDummy();
    }

    private static TextArea createTextArea() {
        final TextArea textBox = new TextArea();
        GwtUtil.setFormControlStyles(textBox);
        return textBox;
    }

    public static TextBox createTextBox() {
        final TextBox textBox = new TextBox();
        GwtUtil.setFormControlStyles(textBox);
        return textBox;
    }

    public static DoubleBox createDoubleBox() {
        final DoubleBox doubleBox = new DoubleBox();
        GwtUtil.setFormControlStyles(doubleBox);
        doubleBox.getElement().setPropertyString("type", "number");
        return doubleBox;
    }

    public static DateBoxWithReadOnly createDateTextBox() {
        final DateBoxWithReadOnly dateBox = new DateBoxWithReadOnly();
        GwtUtil.setFormControlStyles(dateBox.getTextBox());
        return dateBox;
    }

    public static SuggestBox createSuggestBox(SuggestOracle oracle) {
        final SuggestBox suggestBox = new SuggestBox(oracle);
        GwtUtil.setFormControlStyles(suggestBox.getValueBox());
        return suggestBox;
    }
}
