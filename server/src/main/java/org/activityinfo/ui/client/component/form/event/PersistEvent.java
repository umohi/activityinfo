package org.activityinfo.ui.client.component.form.event;
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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author yuriyz on 3/19/14.
 */
public class PersistEvent extends GwtEvent<PersistEvent.Handler> {

    /**
     * Handler of event.
     */
    public static interface Handler extends EventHandler {

        /**
         * Handles state based on event object.
         *
         * @param p_event event object
         */
        void persist(PersistEvent p_event);
    }

    /**
     * Type of event
     */
    public static final Type<Handler> TYPE = new Type<Handler>();

    /**
     * Constructor
     */
    public PersistEvent() {
    }

    /**
     * Returns associated type of event.
     *
     * @return associated type of event
     */
    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Dispatches handling to handler object.
     *
     * @param handler handler object
     */
    @Override
    protected void dispatch(Handler handler) {
        handler.persist(this);
    }
}
