package org.activityinfo.ui.client.widget;
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * @author yuriyz on 4/7/14.
 */
public class CellTable<T> extends com.google.gwt.user.cellview.client.CellTable<T> {

    private final EventBus eventBus = new SimpleEventBus();
    private CellTableAffixer affixer;

    public CellTable(int pageSize, Resources resources) {
        super(pageSize, resources);
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            affixer = new CellTableAffixer(CellTable.this);
                        }
                    });
                }
            }
        });
    }

    @Override
    public TableSectionElement getTableHeadElement() {
        return super.getTableHeadElement();
    }

    public CellTableAffixer getAffixer() {
        return affixer;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
