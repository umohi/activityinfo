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

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.table.InstanceTableView;
import org.activityinfo.ui.client.style.ElementStyle;
import org.activityinfo.ui.client.widget.ConfirmDialog;
import org.activityinfo.ui.client.widget.ConfirmDialogCallback;
import org.activityinfo.ui.client.widget.ConfirmDialogMessages;
import org.activityinfo.ui.client.widget.ConfirmDialogResources;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 4/8/14.
 */
public class DeleteInstanceDialog extends ConfirmDialog {

    public static class DeleteSupplier implements Supplier<Void> {

        private final InstanceTableView tableView;

        public DeleteSupplier(InstanceTableView tableView) {
            this.tableView = tableView;
        }

        @Override
        public void perform(final ConfirmDialogCallback<Void> callback) {
            final Set<Projection> selectedSet = tableView.getTable().getSelectionModel().getSelectedSet();
            final List<Cuid> cuids = Lists.newArrayList();
            for (Projection projection : selectedSet) {
                cuids.add(projection.getRootInstanceId());
            }
            tableView.getResourceLocator().remove(cuids).then(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                    LOGGER.log(Level.FINE, "Failed to remove instances.", caught);
                }

                @Override
                public void onSuccess(Void result) {
                    callback.onSuccess(result);
                    tableView.getTable().reload();
                }
            });
        }
    }

    private static final Logger LOGGER = Logger.getLogger(DeleteInstanceDialog.class.getName());

    public DeleteInstanceDialog(InstanceTableView tableView) {
        super(createResources(tableView), ElementStyle.DANGER, new DeleteSupplier(tableView));
    }

    private static ConfirmDialogResources createResources(InstanceTableView tableView) {
        final Set<Projection> selectedSet = tableView.getTable().getSelectionModel().getSelectedSet();
        final int rowsSize = selectedSet.size();
        final String formClassLabel = tableView.getFormClassLabel();

        final ConfirmDialogMessages confirmMessages = new ConfirmDialogMessages(
                I18N.CONSTANTS.confirmDeletion(),
                I18N.MESSAGES.removeTableRowsConfirmation(rowsSize, formClassLabel),
                I18N.CONSTANTS.delete()
        );

        final ConfirmDialogMessages failedMessages = new ConfirmDialogMessages(
                I18N.CONSTANTS.deletionFailed(),
                I18N.MESSAGES.retryDeletion(rowsSize, formClassLabel),
                I18N.CONSTANTS.retry()
        );

        final ConfirmDialogMessages progressMessages = new ConfirmDialogMessages(
                I18N.CONSTANTS.deletionInProgress(),
                I18N.MESSAGES.deletingRows(rowsSize, formClassLabel),
                I18N.CONSTANTS.deleting()
        );

        return new ConfirmDialogResources(confirmMessages, progressMessages, failedMessages);
    }

}
