package org.activityinfo.core.shared.form;
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

import com.google.gwt.view.client.ProvidesKey;

/**
 * @author yuriyz on 3/5/14.
 */
public class FormInstanceKeyProvider implements ProvidesKey<FormInstance> {

    private static final FormInstanceKeyProvider INSTANCE = new FormInstanceKeyProvider();

    private FormInstanceKeyProvider() {
    }

    @Override
    public Object getKey(FormInstance item) {
        return item != null ? item.getId() : null;
    }

    public static FormInstanceKeyProvider getInstance() {
        return INSTANCE;
    }
}
