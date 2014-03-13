package org.activityinfo.core.server.hash.funnel;
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

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.activityinfo.core.shared.form.FormField;

/**
 * @author yuriyz on 3/6/14.
 */
public enum FormFieldFunnel implements Funnel<FormField> {
    INSTANCE;

    @Override
    public void funnel(FormField from, PrimitiveSink into) {
        LocalizedStringFunnel.INSTANCE.funnel(from.getLabel(), into);
        LocalizedStringFunnel.INSTANCE.funnel(from.getDescription(), into);
        LocalizedStringFunnel.INSTANCE.funnel(from.getUnit(), into);
        EnumFunnel.INSTANCE.funnel(from.getCardinality(), into);
        EnumFunnel.INSTANCE.funnel(from.getType(), into);

        into.putString(from.getCalculation(), Charsets.UTF_8);
    }
}
