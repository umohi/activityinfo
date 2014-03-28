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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.widget.HasReadOnly;
import org.activityinfo.ui.client.widget.coord.GwtCoordinateField;
import org.activityinfo.ui.client.widget.undo.UndoManager;

/**
 * @author yuriyz on 1/31/14.
 */
public class GeographicTextBox extends Composite implements HasValue<AiLatLng>, HasReadOnly {

    public static interface GeographicTextBoxUiBinder extends UiBinder<Widget, GeographicTextBox> {
    }

    private static GeographicTextBoxUiBinder uiBinder = GWT
            .create(GeographicTextBoxUiBinder.class);

    @UiField
    GwtCoordinateField latitude;
    @UiField
    GwtCoordinateField longitude;

    public GeographicTextBox() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public GeographicTextBox(UndoManager undoManager) {
        this();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        latitude.setReadOnly(readOnly);
        longitude.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return latitude.isReadOnly();
    }

    @Override
    public AiLatLng getValue() {
        if (latitude != null && longitude != null) {
            try {
                final Double latitudeValue = latitude.getValue();
                final Double longitudeValue = longitude.getValue();
                if (latitudeValue != null && longitudeValue != null) {
                    return new AiLatLng(latitudeValue, longitudeValue);
                }
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void setValue(AiLatLng value) {
        setValue(value, false);
    }

    @Override
    public void setValue(AiLatLng value, boolean fireEvents) {
        final AiLatLng oldValue = getValue();
        if (value != null) {
            latitude.setValue(value.getLat());
            longitude.setValue(value.getLng());
        } else {
            latitude.setValue(null);
            longitude.setValue(null);
        }
        if (fireEvents) {
            // skip if same value
            if ((oldValue == null && value == null) || (value != null && value.equals(oldValue))) {
                return;
            }
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AiLatLng> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
