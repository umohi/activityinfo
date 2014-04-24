package org.activityinfo.ui.client.component.report.editor.map.symbols;
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
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.ui.client.component.report.view.DrillDownEditor;
import org.discotools.gwt.leaflet.client.events.Event;
import org.discotools.gwt.leaflet.client.events.handler.EventHandler;
import org.discotools.gwt.leaflet.client.jsobject.JSObject;

import java.util.List;

/**
 * @author yuriyz on 4/23/14.
 */
public class LeafletMarkerDrilldownEventHandler implements EventHandler<Event> {

    private final DrillDownEditor drillDownEditor;

    public LeafletMarkerDrilldownEventHandler(Dispatcher dispatcher) {
        this.drillDownEditor = new DrillDownEditor(dispatcher);
    }

    @Override
    public void handle(Event event) {
        final JSObject targetOptions = event.getTarget().getProperty("options");
        final Filter effectiveFilter = new Filter();
        final JSObject siteIdJsObject = targetOptions.getProperty(LeafletMarkerFactory.SITES_JS_FIELD_NAME);
        effectiveFilter.addRestriction(DimensionType.Site, getSiteIds(siteIdJsObject));
        drillDownEditor.drillDown(effectiveFilter);
    }

    private static List<Integer> getSiteIds(JSObject jsObject) {
        final List<Integer> siteIds = Lists.newArrayList();
        final String propertyNames = jsObject.getPropertyNames();
        final String[] propertyNamesArray = propertyNames.split(",");
        for (String propertyName : propertyNamesArray) {
            try {
                siteIds.add(Integer.parseInt(propertyName));
            } catch (Exception e) {
                // ignore
            }
        }
        return siteIds;
    }

    public void setPosition(int bottomX, int bottomY) {
        drillDownEditor.setPosition(bottomX - DrillDownEditor.WIDTH, bottomY - DrillDownEditor.HEIGHT);
    }
}
