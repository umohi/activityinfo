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

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuriyz on 2/17/14.
 */
public class UndoManager {

    private final List<IsUndoable> undoables = Lists.newArrayList();
    private final List<UndoListener> listeners = Lists.newArrayList();
    private int position = 0;

    public UndoManager() {
    }

    public void addUndoable(IsUndoable undoable) {
        undoables.add(undoable);
        position++;
        fireCreatedEvent(new UndoableCreatedEvent(undoable));
    }

    public void undo() {
        final IsUndoable last = getLast();
        if (last != null) {
            last.undo();
            fireExecutedEvent(new UndoableExecutedEvent(last));
        }
    }

    public boolean canUndo() {
        return getLast() != null;
    }

    private IsUndoable getLast() {
        return !undoables.isEmpty() ? undoables.get(undoables.size() - 1) : null;
    }

    public boolean canRedo() {
        return position < undoables.size();
    }

    public void redo() {
        final IsUndoable last = getLast();
        if (last != null) {
            last.undo();
            fireExecutedEvent(new UndoableExecutedEvent(last));
        }
    }

    private void fireExecutedEvent(UndoableExecutedEvent undoEvent) {
        for (UndoListener listener : listeners) {
            listener.onUndoableExecuted(undoEvent);
        }
    }

    private void fireCreatedEvent(UndoableCreatedEvent undoEvent) {
        for (UndoListener listener : listeners) {
            listener.onUndoableCreated(undoEvent);
        }
    }

    public void addListener(UndoListener listener) {
        listeners.add(listener);
    }

    public void removeListener(UndoListener listener) {
        listeners.remove(listener);
    }
}
