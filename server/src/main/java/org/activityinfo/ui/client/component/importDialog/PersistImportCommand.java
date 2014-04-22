package org.activityinfo.ui.client.component.importDialog;
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

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.strategy.FieldImporter;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;

import javax.annotation.Nullable;

/**
 * @author yuriyz on 4/18/14.
 */
public class PersistImportCommand implements ImportCommand<Void> {

    private ImportCommandExecutor commandExecutor;

    @Nullable
    @Override
    public Void apply(Void input) {
        final ImportModel model = commandExecutor.getImportModel();

        final Cuid formClassId = ((MapExistingAction)model.getColumnActions().values().iterator().next()).getTarget().getFormClassId();
        for (SourceRow row : model.getSource().getRows()) {
            // new instance per row
            FormInstance newInstance = new FormInstance(CuidAdapter.newFormInstance(formClassId), formClassId);
            for (FieldImporter importer : commandExecutor.getImporters()) {
                importer.updateInstance(row, newInstance);
            }
            commandExecutor.getResourceLocator().persist(newInstance);
        }

        return null;
    }

    public void setCommandExecutor(ImportCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
}
