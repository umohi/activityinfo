package org.activityinfo.ui.full.client.widget.undo;
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
import com.google.gwt.user.client.ui.HasValue;

/**
 * Creates undoable.
 *
 * @author yuriyz on 2/17/14.
 */
public class UndoableCreator {

    private UndoableCreator() {
    }

    public static IsUndoable create(ValueChangeEvent event, Object oldValue) {
        final Object source = event.getSource();
        if (source instanceof HasValue) {
            return new UndoableEditOperation((HasValue) source, oldValue);
        }
        return null;
    }
}
