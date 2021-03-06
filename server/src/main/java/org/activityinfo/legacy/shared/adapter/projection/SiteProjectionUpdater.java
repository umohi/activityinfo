package org.activityinfo.legacy.shared.adapter.projection;
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

import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.SiteDTO;

/**
 * @author yuriyz on 4/17/14.
 */
public class SiteProjectionUpdater implements ProjectionUpdater<SiteDTO> {

    private final FieldPath path;
    private final int fieldIndex;

    public SiteProjectionUpdater(FieldPath path, int fieldIndex) {
        this.path = path;
        this.fieldIndex = fieldIndex;
    }

    @Override
    public void update(Projection projection, SiteDTO dto) {
        switch (fieldIndex) {
            case CuidAdapter.START_DATE_FIELD:
                projection.setValue(path, dto.getDate1());
                break;
            case CuidAdapter.END_DATE_FIELD:
                projection.setValue(path, dto.getDate2());
                break;
            case CuidAdapter.COMMENT_FIELD:
                projection.setValue(path, dto.getComments());
                break;
        }
    }
}
