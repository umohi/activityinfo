package org.activityinfo.ui.client.page.report.json;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.reports.model.AdminDimension;
import org.activityinfo.legacy.shared.reports.model.Dimension;
import org.activityinfo.legacy.shared.reports.model.PivotChartReportElement;
import org.activityinfo.legacy.shared.reports.model.Report;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Ignore("not yet being used")
public class ReportSerializerTest {

    private ReportSerializer reportSerializer;
    ReportJsonFactory factory;
    private Report report;
    Long longDate = Long.valueOf("1331712050839");
    private Date maxDate = new Date(longDate);
    private Date minDate = new Date(longDate);

    @Before
    public void setup() {
        reportSerializer = new ReportJsonFactory(new JsonParser());
        factory = new ReportJsonFactory(new JsonParser());

        report = createReport();

    }

    @Test
    public void serializerTest() {

    }

    @Test
    public void deSerializerTest() {
        assertNull(reportSerializer.deserialize(""));
    }

    @Test
    public void fullReportSerializerTest() {

    }

    @Test
    public void fullReportDeSerializerTest() {

    }

    @Test
    public void reportChartElementTest() {

        String chartJson = "{\"elementType\":\"pivotChart\",\"title\":\"Chart Element\",\"type\":\"ClusteredBar\",\"filter\":{\"restrictions\":[]},\"categoryDimensions\":[{\"type\":\"AdminLevel\",\"caption\":\"admin level\",\"level\":0}],\"seriesDimensions\":[]}";

        PivotChartReportElement chartElement = report.getElement(0);
        JsonElement resultJsonElement = factory
                .encodePivotChartReportElement(chartElement);
        System.out.println(resultJsonElement.toString());
        assertEquals(chartJson, resultJsonElement.toString());

        PivotChartReportElement resultChart = factory
                .decodePivotChartReportElement((JsonObject) resultJsonElement);

        assertEquals(chartElement.getTitle(), resultChart.getTitle());
        assertEquals(chartElement.getContent(), resultChart.getContent());
        assertEquals(chartElement.getType(), resultChart.getType());

    }

    @Test
    public void dimensionsTest() {
        createReport();
    }

    @Test
    public void filtersTest() {

        String filterJson = "{\"minDate\":1331679600000,\"maxDate\":1331679600000,\"restrictions\":[{\"type\":\"Activity\",\"set\":[0]}]}";

        JsonElement filterElement = factory.encodeFilter(report.getFilter());
        String resultFiltorJson = filterElement.toString();

        assertEquals(filterJson, resultFiltorJson);

        Filter filter = factory.decodeFilter((JsonObject) filterElement); // filterJson

        assertEquals(report.getFilter().getMaxDate(), filter.getMaxDate());
        assertEquals(report.getFilter().getMinDate(), filter.getMinDate());
    }

    @Test
    public void tableTest() {

    }

    private Report createReport() {
        Report report = new Report();

        report.setTitle("apple report");
        report.setId(0);

        addFilter(report);
        addChartElement(report);

        return report;
    }

    private void addFilter(Report report) {
        Filter filter = new Filter();
        filter.setMaxDate(maxDate);
        filter.setMinDate(minDate);

        filter.addRestriction(DimensionType.Activity, 0);
        report.setFilter(filter);
    }

    private void addChartElement(Report report) {
        PivotChartReportElement chartElement = new PivotChartReportElement();
        chartElement.setTitle("Chart Element");
        chartElement.setCategoryDimensions(createDimensionList());

        report.addElement(chartElement);
    }

    private List<Dimension> createDimensionList() {
        List dims = Lists.newArrayList();

        AdminDimension dim1 = new AdminDimension("admin level", 0);
        dims.add(dim1);

        return dims;
    }
}
