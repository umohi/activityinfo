package org.activityinfo.ui.client.widget;
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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import org.activityinfo.core.shared.type.formatter.DateFormatterFactory;

import java.util.Date;

/**
 * A text box that shows a {@link DatePicker} when the user focuses on it.
 * <p/>
 * <h3>CSS Style Rules</h3>
 * <p/>
 * <dl>
 * <dt>.gwt-DateBox</dt>
 * <dd>default style name</dd>
 * <dt>.dateBoxPopup</dt>
 * <dd>Applied to the popup around the DatePicker</dd>
 * <dt>.dateBoxFormatError</dt>
 * <dd>Default style for when the date box has bad input. Applied by
 * {@link DateBox.DefaultFormat} when the text does not represent a date that
 * can be parsed</dd>
 * </dl>
 * <p/>
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.DateBoxExample}
 * </p>
 */
public class DateBoxWithReadOnly extends DateBox implements HasReadOnly {

    public DateBoxWithReadOnly() {
        setFormat(createFormat());
    }

    public DateBoxWithReadOnly(DatePicker picker, Date date, Format format) {
        super(picker, date, createFormat());
    }

    public static Format createFormat() {
        return new DefaultFormat(DateTimeFormat.getFormat(DateFormatterFactory.FORMAT));
    }

    @Override
    public void showDatePicker() {
        if (!isReadOnly()) {
            super.showDatePicker();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        getTextBox().setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return getTextBox().isReadOnly();
    }
}

