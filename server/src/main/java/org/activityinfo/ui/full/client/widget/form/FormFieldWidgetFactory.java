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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.Log;
import org.activityinfo.ui.full.client.widget.DateBoxWithReadOnly;
import org.activityinfo.ui.full.client.widget.undo.UndoManager;
import org.activityinfo.ui.full.client.widget.undo.UndoableValueBoxBaseOperation;

import java.util.Date;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldWidgetFactory {

    public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_FULL);

    private FormFieldWidgetFactory() {
    }

    public static IsWidget create(FormField field, FormPanel formPanel) {
        final FormFieldType fieldType = field.getType();
        if (fieldType != null) {
            final UndoManager undoManager = formPanel.getUndoManager();
            switch (fieldType) {
                case QUANTITY:
                    return createDoubleBox(undoManager);
                case NARRATIVE:
                    return createTextArea(undoManager);
                case FREE_TEXT:
                    return createTextBox(undoManager);
                case LOCAL_DATE:
                    return createDateTextBox(undoManager);
                case GEOGRAPHIC_POINT:
                    return new GeographicTextBox(undoManager);
                case REFERENCE:
                    return new FormFieldWidgetReference(field, formPanel.getResourceLocator(), undoManager);
                default:
                    Log.error("Field type " + fieldType + " is not supported, created text box widget as fallback.");
            }
        }
        return new FormFieldWidgetDummy();
    }

    public static void addUndoableOnValueChange(final ValueBoxBase valueBoxBase, final UndoManager undoManager) {
        valueBoxBase.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                undoManager.addUndoable(new UndoableValueBoxBaseOperation(valueBoxBase, valueBoxBase.getValue()));
            }
        });
    }

    private static TextArea createTextArea(final UndoManager undoManager) {
        final TextArea textBox = new TextArea();
        textBox.addStyleName("form-control");
        addUndoableOnValueChange(textBox, undoManager);
        return textBox;
    }

    public static TextBox createTextBox(UndoManager undoManager) {
        final TextBox textBox = new TextBox();
        textBox.addStyleName("form-control");
        addUndoableOnValueChange(textBox, undoManager);
        return textBox;
    }

    public static DoubleBox createDoubleBox(UndoManager undoManager) {
        final DoubleBox doubleBox = new DoubleBox();
        doubleBox.addStyleName("form-control");
        doubleBox.getElement().setPropertyString("type", "number");
        addUndoableOnValueChange(doubleBox, undoManager);
        return doubleBox;
    }

    public static DateBoxWithReadOnly createDateTextBox(final UndoManager undoManager) {
        final DateBoxWithReadOnly dateBox = new DateBoxWithReadOnly();
        dateBox.getTextBox().addStyleName("form-control");
        dateBox.setFormat(new DateBoxWithReadOnly.DefaultFormat(DATE_TIME_FORMAT));
        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                // todo
                //undoManager.addUndoable(new UndoableValueBoxBaseOperation(valueBoxBase));
            }
        });
        return dateBox;
    }

    public static SuggestBox createSuggestBox(SuggestOracle oracle, UndoManager undoManager) {
        final SuggestBox suggestBox = new SuggestBox(oracle);
        suggestBox.getValueBox().addStyleName("form-control");
        suggestBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // todo
                //undoManager.addUndoable(new UndoableValueBoxBaseOperation(valueBoxBase));
            }
        });
        return suggestBox;
    }
}
