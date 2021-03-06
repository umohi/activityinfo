package org.activityinfo.legacy.shared.impl.search;

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

import com.bedatadriven.rebar.sql.client.*;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.legacy.shared.command.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class GenericSearcher implements Searcher {
    private DimensionType dimension;

    public GenericSearcher(DimensionType dimension) {
        this.dimension = dimension;
    }

    @Override
    public void search(List<String> testQuery, SqlTransaction tx,
                       final AsyncCallback<List<Integer>> callback) {
        final List<Integer> ids = new ArrayList<Integer>();
        String tableName = dimension.toString();
        final String primaryKey = tableName + "Id";

        SqlQuery.select(primaryKey)
                .from(tableName.toLowerCase())
                .whereLikes("Name")
                .likeMany(testQuery)

                .execute(tx, new SqlResultCallback() {
                    @Override
                    public void onSuccess(SqlTransaction tx, SqlResultSet results) {
                        for (SqlResultSetRow row : results.getRows()) {
                            ids.add(row.getInt(primaryKey));
                        }
                        callback.onSuccess(ids);
                    }

                    @Override
                    public boolean onFailure(SqlException e) {
                        return super.onFailure(e);
                    }
                });
    }

    @Override
    public DimensionType getDimensionType() {
        return dimension;
    }

}
