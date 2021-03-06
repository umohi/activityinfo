package org.activityinfo.legacy.shared.command;

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

import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.reports.model.AttributeGroupDimension;
import org.activityinfo.legacy.shared.reports.model.Dimension;
import org.activityinfo.legacy.shared.reports.model.PivotTableReportElement;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.TestDatabaseModule;
import org.activityinfo.server.report.ReportModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(InjectionSupport.class)
@Modules({TestDatabaseModule.class, ReportModule.class})
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GeneratePivotTableHandlerTest extends CommandTestCase2 {

    @Test
    public void serverSide() throws CommandException {

        PivotTableReportElement element = new PivotTableReportElement();
        element.setRowDimensions(Arrays.asList(new Dimension(
                DimensionType.Indicator)));
        element.setColumnDimensions(Arrays.asList(new Dimension(
                DimensionType.Partner)));

        Filter filter = new Filter();
        filter
                .addRestriction(DimensionType.Indicator, Arrays.asList(1, 2, 103));
        element.setFilter(filter);

        execute(new GeneratePivotTable(element));

        // TODO real test
        // System.out.println(content.getData());

    }

    @Test
    public void withNullAttribute() throws CommandException {
        PivotTableReportElement element = new PivotTableReportElement();
        element.setRowDimensions(Arrays.asList(new Dimension(
                DimensionType.Indicator)));
        element.setColumnDimensions(Arrays
                .asList((Dimension) new AttributeGroupDimension(1)));

        Filter filter = new Filter();
        filter
                .addRestriction(DimensionType.Indicator, Arrays.asList(1, 2, 103));
        element.setFilter(filter);

        execute(new GeneratePivotTable(element));

    }

}
