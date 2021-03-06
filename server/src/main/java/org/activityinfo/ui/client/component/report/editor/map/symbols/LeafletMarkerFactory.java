package org.activityinfo.ui.client.component.report.editor.map.symbols;

import org.activityinfo.legacy.shared.reports.content.BubbleMapMarker;
import org.activityinfo.legacy.shared.reports.content.IconMapMarker;
import org.activityinfo.legacy.shared.reports.content.MapMarker;
import org.activityinfo.legacy.shared.reports.content.PieMapMarker;
import org.activityinfo.legacy.shared.reports.content.PieMapMarker.SliceValue;
import org.activityinfo.legacy.shared.reports.model.MapIcon;
import org.activityinfo.ui.client.util.LeafletUtil;
import org.discotools.gwt.leaflet.client.Options;
import org.discotools.gwt.leaflet.client.events.handler.EventHandler;
import org.discotools.gwt.leaflet.client.events.handler.EventHandlerManager;
import org.discotools.gwt.leaflet.client.jsobject.JSObject;
import org.discotools.gwt.leaflet.client.marker.CircleMarker;
import org.discotools.gwt.leaflet.client.marker.Marker;
import org.discotools.gwt.leaflet.client.marker.MarkerOptions;
import org.discotools.gwt.leaflet.client.types.Icon;
import org.discotools.gwt.leaflet.client.types.IconOptions;
import org.discotools.gwt.leaflet.client.types.LatLng;
import org.discotools.gwt.leaflet.client.types.Point;

import java.util.List;

public class LeafletMarkerFactory {

    public static final String SITES_JS_FIELD_NAME = "sites";

    public static Marker create(MapMarker mapMarker, final EventHandler... markerEventHandlers) {
        final Marker marker;
        if (mapMarker instanceof IconMapMarker) {
            marker = createIconMapMarker((IconMapMarker) mapMarker);
        } else if (mapMarker instanceof PieMapMarker) {
            marker = createPieMapMarker((PieMapMarker) mapMarker);
        } else if (mapMarker instanceof BubbleMapMarker) {
            marker = createBubbleMapMarker((BubbleMapMarker) mapMarker);
        } else {
            final Options options = new Options();
            options.setProperty(SITES_JS_FIELD_NAME, createSiteIdsJSObject(mapMarker.getSiteIds()));
            marker = new Marker(toLatLng(mapMarker), options);
        }

        if (markerEventHandlers != null) {
            for (EventHandler handler : markerEventHandlers) {
                EventHandlerManager.addEventHandler(marker, EventHandler.Events.click, handler);
            }
        }
        return marker;
    }

    public static JSObject createSiteIdsJSObject(List<Integer> siteIds) {
        final JSObject sitesJson = JSObject.createJSObject();
        for (Integer siteId : siteIds) {
            sitesJson.setProperty(siteId.toString(), siteId);
        }
        return sitesJson;
    }


    /**
     * Creates a Leaflet marker based on an ActivityInfo MapIcon
     */
    public static Marker createIconMapMarker(IconMapMarker model) {
        MapIcon iconModel = model.getIcon();
        String iconUrl = "mapicons/" + iconModel.getName() + ".png";

        IconOptions iconOptions = new IconOptions();
        iconOptions.setIconUrl(iconUrl);
        iconOptions.setIconAnchor(new Point(iconModel.getAnchorX(), iconModel.getAnchorY()));
        iconOptions.setIconSize(new Point(iconModel.getWidth(), iconModel.getHeight()));

        Options markerOptions = new MarkerOptions();
        markerOptions.setProperty("icon", new Icon(iconOptions));
        markerOptions.setProperty(SITES_JS_FIELD_NAME, createSiteIdsJSObject(model.getSiteIds()));

        return new Marker(toLatLng(model), markerOptions);
    }

    private static LatLng toLatLng(MapMarker model) {
        return new LatLng(model.getLat(), model.getLng());
    }

    public static Marker createBubbleMapMarker(BubbleMapMarker marker) {

        Options options = new Options();
        options.setProperty("radius", marker.getRadius());
        options.setProperty("fill", true);
        options.setProperty("fillColor", LeafletUtil.color(marker.getColor()));
        options.setProperty("fillOpacity", marker.getAlpha());
        options.setProperty("color", LeafletUtil.color(marker.getColor())); // stroke color
        options.setProperty("opacity", 0.8); // stroke opacity
        options.setProperty(SITES_JS_FIELD_NAME, createSiteIdsJSObject(marker.getSiteIds()));

        return new CircleMarker(toLatLng(marker), options);
    }

    public static Marker createPieMapMarker(PieMapMarker marker) {
        StringBuilder sb = new StringBuilder();
        sb.append("icon?t=piechart&r=").append(marker.getRadius());
        for (SliceValue slice : marker.getSlices()) {
            sb.append("&value=").append(slice.getValue());
            sb.append("&color=").append(slice.getColor());
        }
        String iconUrl = sb.toString();
        int size = marker.getRadius() * 2;

        IconOptions iconOptions = new IconOptions();
        iconOptions.setIconUrl(iconUrl);
        iconOptions.setIconAnchor(new Point(marker.getRadius(), marker.getRadius()));
        iconOptions.setIconSize(new Point(size, size));

        Options markerOptions = new MarkerOptions();
        markerOptions.setProperty("icon", new Icon(iconOptions));
        markerOptions.setProperty(SITES_JS_FIELD_NAME, createSiteIdsJSObject(marker.getSiteIds()));

        return new Marker(toLatLng(marker), markerOptions);
    }

}
