package org.activityinfo.api.shared.impl;

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

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.activityinfo.api.shared.command.GetLocations;
import org.activityinfo.api.shared.command.GetLocations.GetLocationsResult;
import org.activityinfo.api.shared.model.AdminEntityDTO;
import org.activityinfo.api.shared.model.LocationDTO;
import org.activityinfo.reports.shared.model.DimensionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLocationsHandler implements
        CommandHandlerAsync<GetLocations, GetLocationsResult> {

    @Override
    public void execute(final GetLocations command,
                        final ExecutionContext context,
                        final AsyncCallback<GetLocationsResult> callback) {
        if (command.hasLocationIds()) {
            final Map<Integer, LocationDTO> dtos = new HashMap<Integer, LocationDTO>();

            SqlQuery.select("locationID", "name", "axe", "x", "y", "workflowStatusId")
                    .from(Tables.LOCATION)
                    .where("locationId").in(command.getLocationIds())
                    .execute(context.getTransaction(), new SqlResultCallback() {
                        @Override
                        public void onSuccess(SqlTransaction tx,
                                              SqlResultSet results) {
                            for (SqlResultSetRow row : results.getRows()) {
                                final LocationDTO dto = new LocationDTO();
                                dto.setId(row.getInt("locationID"));
                                dto.setName(row.getString("name"));
                                dto.setAxe(row.getString("axe"));
                                dto.setWorkflowStatusId(row.getString("workflowStatusId"));
                                if (!row.isNull("x") && !row.isNull("y")) {
                                    dto.setLatitude(row.getDouble("y"));
                                    dto.setLongitude(row.getDouble("x"));
                                }
                                dtos.put(dto.getId(), dto);
                            }

                            SqlQuery
                                    .select()
                                    .appendColumn("AdminEntity.AdminEntityId",
                                            "adminEntityId")
                                    .appendColumn("AdminEntity.Name", "name")
                                    .appendColumn("AdminEntity.AdminLevelId", "levelId")
                                    .appendColumn("link.LocationID", "locationId")
                                    .from(Tables.LOCATION_ADMIN_LINK, "link")
                                    .leftJoin(Tables.ADMIN_ENTITY, "AdminEntity")
                                    .on("link.AdminEntityId=AdminEntity.AdminEntityId")
                                    .where("link.LocationId")
                                    .in(command.getLocationIds())
                                    .execute(context.getTransaction(),
                                            new SqlResultCallback() {
                                                @Override
                                                public void onSuccess(SqlTransaction tx,
                                                                      SqlResultSet results) {
                                                    for (SqlResultSetRow row : results
                                                            .getRows()) {
                                                        AdminEntityDTO entity = new AdminEntityDTO();
                                                        entity.setId(row
                                                                .getInt("adminEntityId"));
                                                        entity.setName(row
                                                                .getString("name"));
                                                        entity.setLevelId(row
                                                                .getInt("levelId"));

                                                        LocationDTO dto = dtos.get(row
                                                                .getInt("locationId"));
                                                        if (dto != null) {
                                                            dto.setAdminEntity(
                                                                    entity.getLevelId(), entity);
                                                        }
                                                    }

                                                    List<LocationDTO> list = new ArrayList<LocationDTO>(
                                                            dtos.values());
                                                    callback
                                                            .onSuccess(new GetLocationsResult(
                                                                    list));
                                                }
                                            });
                        }
                    });
        } else if(command.getFilter() != null ) {
            if(!command.getFilter().isRestricted(DimensionType.Database) &&
                !command.getFilter().isRestricted(DimensionType.Activity) &&
                !command.getFilter().isRestricted(DimensionType.Indicator)) {
                callback.onSuccess(new GetLocationsResult());
            } else {
                final Map<Integer, LocationDTO> dtos = new HashMap<Integer, LocationDTO>();

                SqlQuery.select("locationID", "name", "axe", "x", "y", "workflowStatusId")
                    .from(Tables.LOCATION)
                    .execute(context.getTransaction(), new SqlResultCallback() {
                        @Override
                        public void onSuccess(SqlTransaction tx,
                                              SqlResultSet results) {
                            for (SqlResultSetRow row : results.getRows()) {
                                final LocationDTO dto = new LocationDTO();
                                dto.setId(row.getInt("locationID"));
                                dto.setName(row.getString("name"));
                                dto.setAxe(row.getString("axe"));
                                dto.setWorkflowStatusId(row.getString("workflowStatusId"));
                                if (!row.isNull("x") && !row.isNull("y")) {
                                    dto.setLatitude(row.getDouble("y"));
                                    dto.setLongitude(row.getDouble("x"));
                                }
                                dtos.put(dto.getId(), dto);
                            }

                            SqlQuery
                                    .select()
                                    .appendColumn("AdminEntity.AdminEntityId",
                                            "adminEntityId")
                                    .appendColumn("AdminEntity.Name", "name")
                                    .appendColumn("AdminEntity.AdminLevelId", "levelId")
                                    .appendColumn("link.LocationID", "locationId")
                                    .from(Tables.LOCATION_ADMIN_LINK, "link")
                                    .leftJoin(Tables.ADMIN_ENTITY, "AdminEntity")
                                    .on("link.AdminEntityId=AdminEntity.AdminEntityId")
                                    .execute(context.getTransaction(),
                                            new SqlResultCallback() {
                                                @Override
                                                public void onSuccess(SqlTransaction tx,
                                                                      SqlResultSet results) {
                                                    for (SqlResultSetRow row : results
                                                            .getRows()) {
                                                        AdminEntityDTO entity = new AdminEntityDTO();
                                                        entity.setId(row
                                                                .getInt("adminEntityId"));
                                                        entity.setName(row
                                                                .getString("name"));
                                                        entity.setLevelId(row
                                                                .getInt("levelId"));

                                                        LocationDTO dto = dtos.get(row
                                                                .getInt("locationId"));
                                                        if (dto != null) {
                                                            dto.setAdminEntity(
                                                                    entity.getLevelId(), entity);
                                                        }
                                                    }

                                                    List<LocationDTO> list = new ArrayList<LocationDTO>(
                                                            dtos.values());
                                                    callback
                                                            .onSuccess(new GetLocationsResult(
                                                                    list));
                                                }
                                            });
                        }
                    });
            }
        }
    }

}
