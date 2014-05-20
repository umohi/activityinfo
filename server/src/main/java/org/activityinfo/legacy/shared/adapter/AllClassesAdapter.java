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
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.legacy.shared.model.*;

import java.util.List;

/**
 * @author yuriyz on 3/17/14.
 */
public class AllClassesAdapter implements Function<SchemaDTO, List<FormClass>> {

    @Override
    public List<FormClass> apply(SchemaDTO schema) {
        final List<FormClass> result = Lists.newArrayList();

        // gather all possible form classes
        for (UserDatabaseDTO database : schema.getDatabases()) {
            for (ActivityDTO activityDTO : database.getActivities()) {
                final ActivityUserFormBuilder builder = new ActivityUserFormBuilder(activityDTO);
                result.add(builder.build());

                for (AttributeGroupDTO attributeGroupDTO : activityDTO.getAttributeGroups()) {
                    AttributeClassAdapter adapter = new AttributeClassAdapter(attributeGroupDTO.getId());
                    result.add(adapter.apply(schema));
                }

                final LocationTypeDTO locationType = activityDTO.getLocationType();
                if (locationType.isAdminLevel()) {
                    final AdminLevelClassAdapter adminLevelClassAdapter = new AdminLevelClassAdapter(locationType
                            .getBoundAdminLevelId());
                    result.add(adminLevelClassAdapter.apply(schema));
                } else {
                    final LocationClassAdapter locationClassAdapter = new LocationClassAdapter(locationType.getId());
                    result.add(locationClassAdapter.apply(schema));
                }
            }

            result.add(PartnerClassAdapter.create(database.getId()));
        }


        return result;
    }
}
