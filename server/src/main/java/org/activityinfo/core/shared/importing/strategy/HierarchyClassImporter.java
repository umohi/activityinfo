package org.activityinfo.core.shared.importing.strategy;
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

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.fp.client.Promise;

import java.util.List;

/**
 * @author yuriyz on 5/19/14.
 */
public class HierarchyClassImporter implements FieldImporter {
    @Override
    public Promise<Void> prepare(ResourceLocator locator, List<SourceRow> batch) {
        return null;
    }

    @Override
    public void validateInstance(SourceRow row, List<ValidationResult> results) {

    }

    @Override
    public boolean updateInstance(SourceRow row, FormInstance instance) {
        return false;
    }

    @Override
    public List<FieldImporterColumn> getColumns() {
        return null;
    }
}
