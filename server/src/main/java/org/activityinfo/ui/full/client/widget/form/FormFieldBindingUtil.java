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

import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.ui.full.client.widget.coord.CoordinateField;
import org.activityinfo.ui.full.client.widget.coord.GwtCoordinateField;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldBindingUtil {

    private FormFieldBindingUtil() {
    }

    public static FormFieldBinding create(FormField field) {
        final FormFieldType fieldType = field.getType();
        if (fieldType != null) {
            switch (fieldType) {
                case QUANTITY:
                    return new FormFieldQuantityBinding(createDoubleBox(), field);
                case FREE_TEXT:
                    return new FormFieldTextBinding(createTextBox(), field);
                case LOCAL_DATE:
                    return new FormFieldDateBinding(createDateTextBox(), field);
                case GEOGRAPHIC_POINT:
                    final GwtCoordinateField latitude = new GwtCoordinateField(CoordinateField.Axis.LATITUDE);
                    final GwtCoordinateField longitude = new GwtCoordinateField(CoordinateField.Axis.LONGITUDE);
                    return new FormFieldGeographicBinding(latitude, longitude, field);
                case REFERENCE:
                    return new FormFieldTextBinding(createTextBox(), field);
            }
        }
        return null;
    }

    public static TextBox createTextBox() {
        final TextBox textBox = new TextBox();
        textBox.addStyleName("form-control");
        // TEST CODE MUST BE REMOVED LATER!!!
//        textBox.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                textBox.getTabIndex();
//            }
//        });
//        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<String> event) {
//                textBox.getTabIndex();
//            }
//        });
        return textBox;
    }

    public static DoubleBox createDoubleBox() {
        final DoubleBox doubleBox = new DoubleBox();
        doubleBox.addStyleName("form-control");
        doubleBox.getElement().setPropertyString("type", "number");
        return doubleBox;
    }

    public static DateBox createDateTextBox() {
        final DateBox dateBox = new DateBox();
        dateBox.getTextBox().addStyleName("form-control");

        // TEST CODE MUST BE REMOVED LATER!!!
//        DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");
//        dateBox.getTextBox().addKeyUpHandler(new KeyUpHandler() {
//            public void onKeyUp(KeyUpEvent event) {
//                updateSelectionLabel(dateBox);
//            }
//        });
//        dateBox.setFormat(new DateBox.DefaultFormat(df));
//        // Add a ClickHandler
//        dateBox.getTextBox().addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                updateSelectionLabel(dateBox);
//            }
//        });
//        dateBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<String> event) {
//                updateSelectionLabel(dateBox);
//            }
//        });
//        dateBox.setValue("It's date value");
//        dateBox.addStyleName("form-control");
//        dateBox.addStyleName("gwt-DateBox");
//        dateBox.getElement().setPropertyString("type", "date");
        return dateBox;
    }

//    private static void updateSelectionLabel(DateBox dateBox) {
//        final Date value = dateBox.getDateValue();
//        final String valueAsString = dateBox.getText();
//        final DateBox.Format format = dateBox.getFormat();
//    }
}
