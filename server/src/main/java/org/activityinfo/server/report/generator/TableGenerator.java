package org.activityinfo.server.report.generator;

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

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.reports.content.TableContent;
import org.activityinfo.legacy.shared.reports.content.TableData;
import org.activityinfo.legacy.shared.reports.model.DateRange;
import org.activityinfo.legacy.shared.reports.model.TableColumn;
import org.activityinfo.legacy.shared.reports.model.TableElement;
import org.activityinfo.server.command.DispatcherSync;
import org.activityinfo.server.database.hibernate.entity.User;

import java.util.Collections;
import java.util.Map;

public class TableGenerator extends ListGenerator<TableElement> {

    private MapGenerator mapGenerator;

    @Inject
    public TableGenerator(DispatcherSync dispatcher, MapGenerator mapGenerator) {
        super(dispatcher);
        this.mapGenerator = mapGenerator;
    }

    @Override
    public void generate(User user, TableElement element,
                         Filter inheritedFilter, DateRange dateRange) {
        Filter filter = GeneratorUtils.resolveElementFilter(element, dateRange);
        Filter effectiveFilter = inheritedFilter == null ? filter : new Filter(
                inheritedFilter, filter);

        TableContent content = new TableContent();
        content.setFilterDescriptions(generateFilterDescriptions(filter,
                Collections.<DimensionType>emptySet(), user));

        TableData data = generateData(element, effectiveFilter);
        content.setData(data);

        if (element.getMap() != null) {
            mapGenerator.generate(user, element.getMap(), effectiveFilter,
                    dateRange);

            Map<Integer, String> siteLabels = element.getMap().getContent()
                    .siteLabelMap();
            for (SiteDTO row : data.getRows()) {
                row.set("map", siteLabels.get(row.getId()));
            }
        }
        element.setContent(content);
    }

    public TableData generateData(TableElement element, Filter filter) {
        GetSites query = new GetSites(filter);

        if (!element.getSortBy().isEmpty()) {
            TableColumn sortBy = element.getSortBy().get(0);
            query.setSortInfo(new SortInfo(sortBy.getSitePropertyName(), sortBy
                    .isOrderAscending() ? SortDir.ASC : SortDir.DESC));
        }

        SiteResult sites = getDispatcher().execute(query);
        return new TableData(element.getRootColumn(), sites.getData());
    }
}
