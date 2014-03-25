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

import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.widget.DateBoxWithReadOnly;
import org.activityinfo.ui.client.widget.DoubleBox;
import org.activityinfo.ui.client.widget.TextArea;
import org.activityinfo.ui.client.widget.TextBox;
import org.activityinfo.ui.client.widget.undo.UndoableCreator;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldWidgetFactory {

    private FormFieldWidgetFactory() {
    }

    public static IsWidget create(FormField field, FormPanel formPanel) {
        final IsWidget widget = createWidget(field, formPanel.getResourceLocator());
        addValueChangeHandler(widget, field, formPanel);
        addKeyUpHandlers(widget, formPanel);
        return widget;
    }

    private static void addValueChangeHandler(IsWidget widget, final FormField formField, final FormPanel formPanel) {
        if (widget instanceof HasValueChangeHandlers) {
            final HasValueChangeHandlers hasValueChangeHandlers = (HasValueChangeHandlers) widget;
            hasValueChangeHandlers.addValueChangeHandler(new ValueChangeHandler() {
                @Override
                public void onValueChange(ValueChangeEvent event) {
                    final FormInstance formInstance = formPanel.getValue();
                    final Object oldValue = formInstance.get(formField.getId());
                    formPanel.getUndoManager().addUndoable(UndoableCreator.create(event, oldValue)); // push undoable
                    formInstance.set(formField.getId(), event.getValue());
                    formPanel.fireState();
                }
            });
        }
    }

    private static void addKeyUpHandlers(IsWidget widget, final FormPanel formPanel) {
        if (widget instanceof HasKeyUpHandlers) {
            final HasKeyUpHandlers hasKeyUpHandlers = (HasKeyUpHandlers) widget;
            hasKeyUpHandlers.addKeyUpHandler(new KeyUpHandler() {
                @Override
                public void onKeyUp(KeyUpEvent event) {
                    formPanel.fireState();
                }
            });
        }
    }

    public static IsWidget createWidget(FormField field, ResourceLocator resourceLocator) {
        final FormFieldType fieldType = field.getType();
        if (fieldType != null) {
            switch (fieldType) {
                case QUANTITY:
                    return new DoubleBox();
                case NARRATIVE:
                    return new TextArea();
                case FREE_TEXT:
                    return new TextBox();
                case LOCAL_DATE:
                    return new DateBoxWithReadOnly();
                case GEOGRAPHIC_POINT:
                    return new GeographicTextBox();
                case REFERENCE:
                    return new FormFieldWidgetReference(field, resourceLocator);
                default:
                    Log.error("Field type " + fieldType + " is not supported, created text box widget as fallback.");
            }
        }
        return new FormFieldWidgetDummy();
    }
}
