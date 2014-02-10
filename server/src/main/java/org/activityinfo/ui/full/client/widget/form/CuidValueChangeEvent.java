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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HasHandlers;
import org.activityinfo.api2.shared.Cuid;

import java.util.List;

/**
 * @author yuriyz on 2/10/14.
 */
public class CuidValueChangeEvent extends ValueChangeEvent<List<Cuid>> {

    /**
     * Creates a value change event.
     *
     * @param value the value
     */
    protected CuidValueChangeEvent(List<Cuid> value) {
        super(value);
    }

    public static <S extends HasValueChangeHandlers<List<Cuid>> & HasHandlers> void fireIfNotEqual(
            S source, List<Cuid> oldValue, List<Cuid> newValue) {
        if (ValueChangeEvent.shouldFire(source, oldValue, newValue)) {
            source.fireEvent(new CuidValueChangeEvent(newValue));
        }
    }
}
