package org.activityinfo.ui.client.importer.converter;
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

import org.activityinfo.core.client.formatter.GwtDateFormatterFactory;
import org.activityinfo.core.shared.formatter.DateFormatter;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author yuriyz on 3/7/14.
 */
public class StringToDateConverter implements StringConverter<Date> {

    public static final StringToDateConverter INSTANCE = new StringToDateConverter();

    private final static DateFormatter FORMATTER = new GwtDateFormatterFactory().create();

    private StringToDateConverter() {
    }

    @Nonnull
    @Override
    public Date convert(@Nonnull String value) {
        return FORMATTER.parse(value);
    }
}