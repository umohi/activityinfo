package org.activityinfo.dev.client.styles;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.style.BaseStylesheet;

/**
 * @author yuriyz on 2/13/14.
 */
public class Binder extends Composite {

    private static UBinder uiBinder = GWT
            .create(UBinder.class);

    interface UBinder extends UiBinder<Widget, Binder> {
    }

    public Binder() {
        BaseStylesheet.INSTANCE.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
    }
}
