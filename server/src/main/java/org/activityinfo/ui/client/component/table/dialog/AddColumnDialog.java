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

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.widget.ModalDialog;

import java.util.List;

/**
 * @author yuriyz on 3/26/14.
 */
public class AddColumnDialog extends ModalDialog<AddColumnDialogContent> {

    private final AddColumnDialogContent content;

    public AddColumnDialog(final List<FieldColumn> allColumns, final List<FieldColumn> visibleColumns) {
        content = new AddColumnDialogContent(allColumns, visibleColumns);
        setDialogTitle(I18N.CONSTANTS.addInstance());
        getModalBody().add(content);
    }

    @Override
    public AddColumnDialogContent getContent() {
        return content;
    }
}
