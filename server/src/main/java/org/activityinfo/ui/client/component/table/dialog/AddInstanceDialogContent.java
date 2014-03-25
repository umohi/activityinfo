package org.activityinfo.ui.client.component.table.dialog;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import org.activityinfo.ui.client.component.table.InstanceTableView;

/**
 * @author yuriyz on 3/24/14.
 */
public class AddInstanceDialogContent extends Composite {

    interface AddInstanceDialogContentUiBinder extends UiBinder<HTMLPanel, AddInstanceDialogContent> {
    }

    private static AddInstanceDialogContentUiBinder uiBinder = GWT.create(AddInstanceDialogContentUiBinder.class);

    public AddInstanceDialogContent(InstanceTableView tableView, AddInstanceDialog addInstanceDialog) {
        initWidget(uiBinder.createAndBindUi(this));
    }
}
