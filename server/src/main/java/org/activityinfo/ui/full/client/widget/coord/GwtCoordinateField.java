package org.activityinfo.ui.full.client.widget.coord;
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

import com.google.gwt.user.client.ui.TextBox;
import org.activityinfo.ui.full.client.i18n.I18N;

/**
 * @author yuriyz on 1/27/14.
 */
public class GwtCoordinateField extends TextBox {

    private CoordinateEditor editor;

    /**
     * @param axis axis
     */
    public GwtCoordinateField(CoordinateField.Axis axis) {
        super();
        editor = new CoordinateEditor(axis);
        addStyleName("form-control");
        // todo
//        this.setPropertyEditor(editor);
//        this.setValidator(editor);
//        this.setValidateOnBlur(true);
    }

    /**
     * Sets the bounds for this field
     *
     * @param name     the name of the bounds to present to users in the event of
     *                 violation, (e.g. "Kapisa Province Boundary"
     * @param minValue minimum allowed value for this field
     * @param maxValue maximum allowed value for this field
     */
    public void setBounds(String name, double minValue, double maxValue) {
        editor.setMinValue(minValue - CoordinateField.DELTA);
        editor.setMaxValue(maxValue + CoordinateField.DELTA);
        editor.setOutOfBoundsMessage(I18N.MESSAGES.coordOutsideBounds(name));
    }
}
