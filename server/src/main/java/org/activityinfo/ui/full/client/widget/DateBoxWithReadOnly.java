/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.activityinfo.ui.full.client.widget;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

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
public class DateBoxWithReadOnly extends Composite implements HasEnabled, HasValue<Date>,
        IsEditor<LeafValueEditor<Date>>, HasReadOnly {

    public static class DateChangeEvent extends ValueChangeEvent<Date> {

        /**
         * Fires value change event if the old value is not equal to the new value.
         * Use this call rather than making the decision to short circuit yourself for
         * safe handling of null.
         *
         * @param <S>      The event source
         * @param source   the source of the handlers
         * @param oldValue the oldValue, may be null
         * @param newValue the newValue, may be null
         */
        public static <S extends HasValueChangeHandlers<Date> & HasHandlers> void fireIfNotEqualDates(
                S source, Date oldValue, Date newValue) {
            if (ValueChangeEvent.shouldFire(source, oldValue, newValue)) {
                source.fireEvent(new DateChangeEvent(newValue));
            }
        }

        /**
         * Creates a new date value change event.
         *
         * @param value the value
         */
        protected DateChangeEvent(Date value) {
            // The date must be copied in case one handler causes it to change.
            super(CalendarUtil.copyDate(value));
        }

        @Override
        public Date getValue() {
            return CalendarUtil.copyDate(super.getValue());
        }
    }

    /**
     * Default {@link DateBox.Format} class. The date is first parsed using the
     * {@link com.google.gwt.i18n.client.DateTimeFormat} supplied by the user, or
     * {@link com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat#DATE_TIME_MEDIUM} by default.
     * <p>
     * If that fails, we then try to parse again using the default browser date
     * parsing.
     * </p>
     * If that fails, the <code>dateBoxFormatError</code> css style is applied to
     * the {@link DateBoxWithReadOnly}. The style will be removed when either a successful
     * {@link #parse(DateBoxWithReadOnly, String, boolean)} is called or
     * {@link #format(DateBoxWithReadOnly, Date)} is called.
     * <p>
     * Use a different {@link DateBox.Format} instance to change that behavior.
     * </p>
     */
    public static class DefaultFormat implements Format {

        private final DateTimeFormat dateTimeFormat;

        /**
         * Creates a new default format instance.
         */
        @SuppressWarnings("deprecation")
        public DefaultFormat() {
            dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        }

        /**
         * Creates a new default format instance.
         *
         * @param dateTimeFormat the {@link DateTimeFormat} to use with this
         *                       {@link Format}.
         */
        public DefaultFormat(DateTimeFormat dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
        }

        public String format(DateBoxWithReadOnly box, Date date) {
            if (date == null) {
                return "";
            } else {
                return dateTimeFormat.format(date);
            }
        }

        /**
         * Gets the date time format.
         *
         * @return the date time format
         */
        public DateTimeFormat getDateTimeFormat() {
            return dateTimeFormat;
        }

        @SuppressWarnings("deprecation")
        public Date parse(DateBoxWithReadOnly dateBox, String dateText, boolean reportError) {
            Date date = null;
            try {
                if (dateText.length() > 0) {
                    date = dateTimeFormat.parse(dateText);
                }
            } catch (IllegalArgumentException exception) {
                try {
                    date = new Date(dateText);
                    // We have parsed the text at higher resolution so do a round trip to get rid of extra:
                    date = dateTimeFormat.parse(dateTimeFormat.format(date));
                } catch (IllegalArgumentException e) {
                    if (reportError) {
                        dateBox.addStyleName(DATE_BOX_FORMAT_ERROR);
                    }
                    return null;
                }
            }
            return date;
        }

        public void reset(DateBoxWithReadOnly dateBox, boolean abandon) {
            dateBox.removeStyleName(DATE_BOX_FORMAT_ERROR);
        }
    }

    /**
     * Implemented by a delegate to handle the parsing and formating of date
     * values. The default {@link Format} uses a new {@link DefaultFormat}
     * instance.
     */
    public interface Format {

        /**
         * Formats the provided date. Note, a null date is a possible input.
         *
         * @param dateBox the date box you are formatting
         * @param date    the date to format
         * @return the formatted date as a string
         */
        String format(DateBoxWithReadOnly dateBox, Date date);

        /**
         * Parses the provided string as a date.
         *
         * @param dateBox     the date box
         * @param text        the string representing a date
         * @param reportError should the formatter indicate a parse error to the
         *                    user?
         * @return the date created, or null if there was a parse error
         */
        Date parse(DateBoxWithReadOnly dateBox, String text, boolean reportError);

        /**
         * If the format did any modifications to the date box's styling, reset them
         * now.
         *
         * @param abandon true when the current format is being replaced by another
         * @param dateBox the date box
         */
        void reset(DateBoxWithReadOnly dateBox, boolean abandon);
    }

    private class DateBoxHandler implements ValueChangeHandler<Date>,
            FocusHandler, BlurHandler, ClickHandler, KeyDownHandler,
            CloseHandler<PopupPanel> {

        public void onBlur(BlurEvent event) {
            if (isDatePickerShowing() == false) {
                updateDateFromTextBox();
            }
        }

        public void onClick(ClickEvent event) {
            showDatePicker();
        }

        public void onClose(CloseEvent<PopupPanel> event) {
            // If we are not closing because we have picked a new value, make sure the
            // current value is updated.
            if (allowDPShow) {
                updateDateFromTextBox();
            }
        }

        public void onFocus(FocusEvent event) {
            if (allowDPShow && isDatePickerShowing() == false) {
                showDatePicker();
            }
        }

        public void onKeyDown(KeyDownEvent event) {
            switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_ENTER:
                case KeyCodes.KEY_TAB:
                    updateDateFromTextBox();
                    // Deliberate fall through
                case KeyCodes.KEY_ESCAPE:
                case KeyCodes.KEY_UP:
                    hideDatePicker();
                    break;
                case KeyCodes.KEY_DOWN:
                    showDatePicker();
                    break;
            }
        }

        public void onValueChange(ValueChangeEvent<Date> event) {
            setValue(parseDate(false), normalize(event.getValue()), true, true);
            hideDatePicker();
            preventDatePickerPopup();
            box.setFocus(true);
        }

        // Round trips on render & parse to convert the date to our interpretation based on the format
        // See issue https://code.google.com/p/google-web-toolkit/issues/detail?id=4785
        private Date normalize(Date date) {
            DateBoxWithReadOnly dateBox = DateBoxWithReadOnly.this;
            return getFormat().parse(dateBox, getFormat().format(dateBox, date), false);
        }
    }

    /**
     * Default style name added when the date box has a format error.
     */
    private static final String DATE_BOX_FORMAT_ERROR = "dateBoxFormatError";

    /**
     * Default style name.
     */
    public static final String DEFAULT_STYLENAME = "gwt-DateBox";
    private static final DefaultFormat DEFAULT_FORMAT = GWT.create(DefaultFormat.class);
    private final PopupPanel popup;
    private final TextBox box = new TextBox();
    private final DatePicker picker;
    private LeafValueEditor<Date> editor;
    private Format format;
    private boolean allowDPShow = true;
    private boolean fireNullValues = false;

    /**
     * Create a date box with a new {@link DatePicker}.
     */
    public DateBoxWithReadOnly() {
        this(new DatePicker(), null, DEFAULT_FORMAT);
    }

    /**
     * Create a new date box.
     *
     * @param date   the default date.
     * @param picker the picker to drop down from the date box
     * @param format to use to parse and format dates
     */
    public DateBoxWithReadOnly(DatePicker picker, Date date, Format format) {
        this.picker = picker;
        this.popup = new PopupPanel(true);
        assert format != null : "You may not construct a date box with a null format";
        this.format = format;

        popup.addAutoHidePartner(box.getElement());
        popup.setWidget(picker);
        popup.setStyleName("dateBoxPopup");

        initWidget(box);
        setStyleName(DEFAULT_STYLENAME);

        DateBoxHandler handler = new DateBoxHandler();
        picker.addValueChangeHandler(handler);
        box.addFocusHandler(handler);
        box.addBlurHandler(handler);
        box.addClickHandler(handler);
        box.addKeyDownHandler(handler);
        box.setDirectionEstimator(false);
        popup.addCloseHandler(handler);
        setValue(date);
    }


    @Override
    public void setReadOnly(boolean readOnly) {
        box.setReadOnly(readOnly);
        allowDPShow = !readOnly; // yuriyz : forbid DP showing if readonly=true
    }

    @Override
    public boolean isReadOnly() {
        return box.isReadOnly();
    }

    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Returns a {@link com.google.gwt.editor.client.adapters.TakesValueEditor} backed by the DateBox.
     */
    public LeafValueEditor<Date> asEditor() {
        if (editor == null) {
            editor = TakesValueEditor.of(this);
        }
        return editor;
    }

    /**
     * Gets the current cursor position in the date box.
     *
     * @return the cursor position
     */
    public int getCursorPos() {
        return box.getCursorPos();
    }

    /**
     * Gets the date picker.
     *
     * @return the date picker
     */
    public DatePicker getDatePicker() {
        return picker;
    }

    /**
     * Returns true iff the date box will fire {@code ValueChangeEvents} with a
     * date value of {@code null} for invalid or empty string values.
     */
    public boolean getFireNullValues() {
        return fireNullValues;
    }

    /**
     * Gets the format instance used to control formatting and parsing of this
     * {@link DateBox}.
     *
     * @return the format
     */
    public Format getFormat() {
        return this.format;
    }

    /**
     * Gets the date box's position in the tab index.
     *
     * @return the date box's tab index
     */
    public int getTabIndex() {
        return box.getTabIndex();
    }

    /**
     * Get text box.
     *
     * @return the text box used to enter the formatted date
     */
    public TextBox getTextBox() {
        return box;
    }

    /**
     * Get the date displayed, or null if the text box is empty, or cannot be
     * interpreted.
     *
     * @return the current date value
     */
    public Date getValue() {
        return parseDate(true);
    }

    /**
     * Hide the date picker.
     */
    public void hideDatePicker() {
        popup.hide();
    }

    /**
     * Returns true if date picker is currently showing, false if not.
     */
    public boolean isDatePickerShowing() {
        return popup.isShowing();
    }

    /**
     * Returns true if the date box is enabled, false if not.
     */
    public boolean isEnabled() {
        return box.isEnabled();
    }

    /**
     * Sets the date box's 'access key'. This key is used (in conjunction with a
     * browser-specific modifier key) to automatically focus the widget.
     *
     * @param key the date box's access key
     */
    public void setAccessKey(char key) {
        box.setAccessKey(key);
    }

    /**
     * Sets whether the date box is enabled.
     *
     * @param enabled is the box enabled
     */
    public void setEnabled(boolean enabled) {
        box.setEnabled(enabled);
    }

    /**
     * Sets whether or not the date box will fire {@code ValueChangeEvents} with a
     * date value of {@code null} for invalid or empty string values.
     */
    public void setFireNullValues(boolean fireNullValues) {
        this.fireNullValues = fireNullValues;
    }

    /**
     * Explicitly focus/unfocus this widget. Only one widget can have focus at a
     * time, and the widget that does will receive all keyboard events.
     *
     * @param focused whether this widget should take focus or release it
     */
    public void setFocus(boolean focused) {
        box.setFocus(focused);
    }

    /**
     * Sets the format used to control formatting and parsing of dates in this
     * {@link DateBox}. If this {@link DateBox} is not empty, the contents of date
     * box will be replaced with current contents in the new format.
     *
     * @param format the new date format
     */
    public void setFormat(Format format) {
        assert format != null : "A Date box may not have a null format";
        if (this.format != format) {
            Date date = getValue();

            // This call lets the formatter do whatever other clean up is required to
            // switch formatters.
            //
            this.format.reset(this, true);

            // Now update the format and show the current date using the new format.
            this.format = format;
            setValue(date);
        }
    }

    /**
     * Sets the date box's position in the tab index. If more than one widget has
     * the same tab index, each such widget will receive focus in an arbitrary
     * order. Setting the tab index to <code>-1</code> will cause this widget to
     * be removed from the tab order.
     *
     * @param index the date box's tab index
     */
    public void setTabIndex(int index) {
        box.setTabIndex(index);
    }

    /**
     * Set the date.
     */
    public void setValue(Date date) {
        setValue(date, false);
    }

    public void setValue(Date date, boolean fireEvents) {
        setValue(picker.getValue(), date, fireEvents, true);
    }

    /**
     * Parses the current date box's value and shows that date.
     */
    public void showDatePicker() {
        // yuriyz : forbid showing date picker is
        if (!allowDPShow) {
            return;
        }
        Date current = parseDate(false);
        if (current == null) {
            current = new Date();
        }
        picker.setCurrentMonth(current);
        popup.showRelativeTo(this);
    }

    private Date parseDate(boolean reportError) {
        if (reportError) {
            getFormat().reset(this, false);
        }
        String text = box.getText().trim();
        return getFormat().parse(this, text, reportError);
    }

    private void preventDatePickerPopup() {
        allowDPShow = false;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            public void execute() {
                allowDPShow = true;
            }
        });
    }

    private void setValue(Date oldDate, Date date, boolean fireEvents, boolean updateText) {
        if (date != null) {
            picker.setCurrentMonth(date);
        }
        picker.setValue(date, false);

        if (updateText) {
            format.reset(this, false);
            box.setText(getFormat().format(this, date));
        }

        if (fireEvents) {
            DateChangeEvent.fireIfNotEqualDates(this, oldDate, date);
        }
    }

    private void updateDateFromTextBox() {
        Date parsedDate = parseDate(true);
        if (fireNullValues || (parsedDate != null)) {
            setValue(picker.getValue(), parsedDate, true, false);
        }
    }
}

