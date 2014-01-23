package org.activityinfo.ui.full.client.dispatch.remote;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api.shared.command.Command;
import org.activityinfo.api.shared.command.result.CommandResult;
import org.activityinfo.ui.full.client.dispatch.AsyncMonitor;
import org.activityinfo.ui.full.client.dispatch.Dispatcher;
import org.activityinfo.ui.full.client.dispatch.monitor.MonitoringCallback;

public abstract class AbstractDispatcher implements Dispatcher {

    @Override
    public final <T extends CommandResult> void execute(Command<T> command,
                                                        AsyncMonitor monitor, AsyncCallback<T> callback) {

        if (monitor == null) {
            execute(command, callback);
        } else {
            execute(command, new MonitoringCallback(monitor, callback));
        }
    }

}
