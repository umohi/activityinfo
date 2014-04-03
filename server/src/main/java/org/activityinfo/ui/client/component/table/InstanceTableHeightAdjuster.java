package org.activityinfo.ui.client.component.table;
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
import org.activityinfo.ui.client.widget.HasScrollAncestor;

/**
 * @author yuriyz on 4/2/14.
 */
public class InstanceTableHeightAdjuster {

    private final InstanceTable table;
    private int tableHeightReduction;

    public InstanceTableHeightAdjuster(InstanceTable table) {
        this.table = table;
    }

    public int getTableHeightReduction() {
        return tableHeightReduction;
    }

    public void setTableHeightReduction(int tableHeightReduction) {
        this.tableHeightReduction = tableHeightReduction;
    }

    public void recalculateTableHeight(int tableHeightReduction) {
        setTableHeightReduction(tableHeightReduction);
        recalculateTableHeight();
    }

    public void recalculateTableHeight() {
        final HasScrollAncestor hasScrollAncestor = table.getHasScrollAncestor();
        if (hasScrollAncestor != null && hasScrollAncestor.getScrollAncestor() != null) {
            final int offsetHeight = hasScrollAncestor.getScrollAncestor().getOffsetHeight();
            if (offsetHeight > 0) {
                // header and links and other ancestors stuff to which we don't have references
                final int ancestorsStuffHeight = 175; // ugly magic number - need better way to calculate it
                final int height = offsetHeight - ancestorsStuffHeight - tableHeightReduction;
                if (height > 0) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            table.getTable().setHeight(height + "px");
                            improveTableHeightIfScrollAppears(height);
                        }
                    });
                }
            }
        }
    }

    private void improveTableHeightIfScrollAppears(int height) {
        final int verticalScrollPosition = table.getHasScrollAncestor().getScrollAncestor().getVerticalScrollPosition();
        if (verticalScrollPosition > 0) {
            final int newHeight = height - 10;
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    table.getTable().setHeight(newHeight + "px");
                    improveTableHeightIfScrollAppears(newHeight);
                }
            });
        }
    }
}
