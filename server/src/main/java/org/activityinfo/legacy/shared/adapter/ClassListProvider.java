package org.activityinfo.legacy.shared.adapter;
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

import com.google.common.base.Function;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.ApplicationClassProvider;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.GetSchema;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yuriyz on 3/17/14.
 */
public class ClassListProvider implements Function<Cuid, Promise<List<FormClass>>> {

    private final Dispatcher dispatcher;
    private final ApplicationClassProvider systemClassProvider = new ApplicationClassProvider();

    public ClassListProvider(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Nullable @Override
    public Promise<List<FormClass>> apply(@Nullable Cuid input) {
        return dispatcher.execute(new GetSchema()).then(new AllClassesAdapter());
    }
}
