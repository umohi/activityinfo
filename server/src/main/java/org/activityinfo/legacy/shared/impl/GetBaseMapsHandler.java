package org.activityinfo.legacy.shared.impl;

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
import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.GetBaseMaps;
import org.activityinfo.legacy.shared.command.result.BaseMapResult;
import org.activityinfo.legacy.shared.model.TileBaseMap;

import java.util.List;

/**
 * @author Alex Bertram
 * @see org.activityinfo.legacy.shared.command.GetBaseMaps
 */
public class GetBaseMapsHandler implements
        CommandHandlerAsync<GetBaseMaps, BaseMapResult> {

    @Override
    public void execute(GetBaseMaps command, ExecutionContext context,
                        final AsyncCallback<BaseMapResult> callback) {

        SqlQuery.select()
                .appendColumn("id")
                .appendColumn("copyright")
                .appendColumn("maxZoom")
                .appendColumn("minZoom")
                .appendColumn("name")
                .appendColumn("tileUrlPattern")
                .appendColumn("thumbnailUrl")
                .from("basemap")
                .execute(context.getTransaction(), new SqlResultCallback() {

                    @Override
                    public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                        List<TileBaseMap> maps = Lists.newArrayList();
                        for (SqlResultSetRow row : results.getRows()) {
                            TileBaseMap map = new TileBaseMap();
                            map.setId(row.getString("id"));
                            map.setCopyright(row.getString("copyright"));
                            map.setMaxZoom(row.getInt("maxZoom"));
                            map.setMinZoom(row.getInt("minZoom"));
                            map.setName(row.getString("name"));
                            map.setTileUrlPattern(row.getString("tileUrlPattern"));
                            map.setThumbnailUrl(row.getString("thumbnailUrl"));
                            maps.add(map);
                        }
                        callback.onSuccess(new BaseMapResult(maps));
                    }
                });
    }
}
