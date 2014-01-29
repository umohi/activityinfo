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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.ui.full.client.widget.coord.GwtCoordinateField;

/**
 * @author yuriyz on 1/27/14.
 */
public class GeographicFormFieldBinding extends FormFieldBinding {

    private GwtCoordinateField latitude;
    private GwtCoordinateField longitude;
    private FormField formField;

    public GeographicFormFieldBinding(GwtCoordinateField latitude, GwtCoordinateField longitude, FormField formField) {
        super(formField);
        this.latitude = latitude;
        this.longitude = longitude;
        this.formField = formField;
    }

    public GwtCoordinateField getLatitude() {
        return latitude;
    }

    public void setLatitude(GwtCoordinateField latitude) {
        this.latitude = latitude;
    }

    public GwtCoordinateField getLongitude() {
        return longitude;
    }

    public void setLongitude(GwtCoordinateField longitude) {
        this.longitude = longitude;
    }

    @Override
    public GwtCoordinateField getControl() {
        return latitude;
    }

    @Override
    public FormField getFormField() {
        return formField;
    }

    @Override
    public Widget getWidget() {
        final HorizontalPanel panel = new HorizontalPanel();
        panel.setSpacing(3);
        panel.add(latitude);
        panel.add(longitude);
        return panel;
    }
}
