/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.map.GcIconFactory;
import org.sigmah.client.map.IconFactory;
import org.sigmah.client.map.MapApiLoader;
import org.sigmah.client.map.MapTypeFactory;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetBaseMaps;
import org.sigmah.shared.command.result.BaseMapResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.map.BaseMap;
import org.sigmah.shared.map.TileBaseMap;
import org.sigmah.shared.report.content.BubbleMapMarker;
import org.sigmah.shared.report.content.IconMapMarker;
import org.sigmah.shared.report.content.MapContent;
import org.sigmah.shared.report.content.MapMarker;
import org.sigmah.shared.report.content.PieMapMarker;
import org.sigmah.shared.report.content.PieMapMarker.SliceValue;
import org.sigmah.shared.report.model.MapReportElement;
import org.sigmah.shared.util.mapping.BoundingBoxDTO;
import org.sigmah.shared.util.mapping.Extents;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapClickHandler.MapClickEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Displays the content of a MapElement using Google Maps.
 * Named AIMapWidget because of a naming conflict with com.google.gwt.maps.client.MapWidget
 */
public class AIMapWidget extends ContentPanel implements HasValue<MapReportElement> {
    private MapWidget mapWidget = null;
    private BaseMap currentBaseMap = null;
    private LatLngBounds pendingZoom = null;

    private Map<Overlay, MapMarker> overlays = new HashMap<Overlay, MapMarker>();
    private Status statusWidget;

    // A map rendered serverside for reporting usage
    private MapReportElement mapReportElement = new MapReportElement();
    
    // Model of a the map
    private MapContent mapModel;
	private List<TileBaseMap> baseMaps;
	
	// True when the first layer is just put on the map
	private boolean isFirstLayerUpdate=true;
	private boolean isGoogleMapsInitialized = false;

    /**
     * True if the Google Maps API is not loaded AND
     * an attempt to load the API has already failed.2 [DEBUG] RemoteDispatcher: sending 3 to server.
     */
    private boolean apiLoadFailed = false;

    private final Dispatcher dispatcher;
	private MapPage mapPage;
    
    public AIMapWidget(Dispatcher dispatcher) {

    	this.dispatcher = dispatcher;
    	
        setHeading(I18N.CONSTANTS.preview());
        setLayout(new FitLayout());

        statusWidget = new Status();
        ToolBar toolBar = new ToolBar();
        toolBar.add(statusWidget);
        setBottomComponent(toolBar);

        // seems like a good time to preload the MapsApi if
        // we haven't already done so

        //MapApiLoader.load();
        
        loadMapAsynchronously();

        addListener(Events.AfterLayout, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (mapWidget != null) {
                    mapWidget.checkResizeAndCenter();
                }
                if (pendingZoom != null) {
                    Log.debug("MapPreview: zooming to " + mapWidget.getBoundsZoomLevel(pendingZoom));
                    mapWidget.setCenter(pendingZoom.getCenter(),
                            mapWidget.getBoundsZoomLevel(pendingZoom));
                }
            }
        });
        
        getBaseMaps();
    }

	private void loadMapAsynchronously() {
		MapApiLoader.load(new MaskingAsyncMonitor(this, I18N.CONSTANTS.loadingMap()),
            new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    apiLoadFailed = false;
                    isGoogleMapsInitialized = true;
                    
                    createGoogleMapWidget();
                    
                    // clear the error message content
                    removeAll();
                    add(mapWidget);
                    updateMapToContent();
                    
                    if (currentBaseMap != null) {
                    	setBaseMap(currentBaseMap);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    handleApiLoadFailure();
                }
            });
	}
    
    public void setBaseMap(BaseMap baseMap) {
    	if (!isGoogleMapsInitialized) {
    		currentBaseMap = baseMap;
    		return;
    	}
    	if (baseMap != null) {
	        if (currentBaseMap == null || !currentBaseMap.equals(baseMap)) {
	            MapType baseMapType = MapTypeFactory.mapTypeForBaseMap(baseMap);
	            mapWidget.removeMapType(MapType.getNormalMap());
	            mapWidget.removeMapType(MapType.getHybridMap());
	            mapWidget.addMapType(baseMapType);
	            mapWidget.setCurrentMapType(baseMapType);
	            currentBaseMap = baseMap;
	            layout();
	        }
    	}
    }
    
    public void createMapIfNeededAndUpdateMapContent() {
        if (mapWidget == null) {

        } else {
            clearOverlays();
            updateMapToContent();
        }
    }
    
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<MapReportElement> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public MapReportElement getValue() {
		return mapReportElement;
	}

	@Override
	public void setValue(MapReportElement value) {
		this.mapReportElement=value;
        if (!apiLoadFailed) {
            clearOverlays();
            createMapIfNeededAndUpdateMapContent();
        }
        ValueChangeEvent.fire(this, mapReportElement);
	}

	@Override
	public void setValue(MapReportElement value, boolean fireEvents) {
		// TODO Auto-generated method stub
		
	}

    private void zoomToBounds(LatLngBounds bounds) {
        int zoomLevel = mapWidget.getBoundsZoomLevel(bounds);

        if (zoomLevel == 0) {
            Log.debug("MapPreview: deferring zoom.");
            pendingZoom = bounds;
        } else {
        	
        	// we want to be careful not to frustrate the user by pulling back 
        	// on the zoom level every time a change is made, so only change the zoom in
        	// if it's an increase from where we are now
        	if(zoomLevel < mapWidget.getZoomLevel()) {
        		zoomLevel = mapWidget.getZoomLevel();
        	}
        	
        	
            Log.debug("MapPreview: zooming to level " + zoomLevel);
            mapWidget.setCenter(bounds.getCenter(), zoomLevel);
            pendingZoom = null;
            DeferredCommand.addCommand(new Command() {
				@Override
				public void execute() {
					zoomToBounds(pendingZoom);
				}
			});
        }
    }

    private LatLngBounds llBoundsForExtents(Extents extents) {
        return LatLngBounds.newInstance(
                LatLng.newInstance(extents.getMinLat(), extents.getMinLon()),
                LatLng.newInstance(extents.getMaxLat(), extents.getMaxLon()));
    }

    protected LatLngBounds llBoundsForExtents(BoundingBoxDTO bounds) {
        return LatLngBounds.newInstance(
                LatLng.newInstance(bounds.getY1(), bounds.getX1()),
                LatLng.newInstance(bounds.getY2(), bounds.getX2()));	
    }


    
	private void createGoogleMapWidget() {
		mapWidget = new MapWidget();
        mapWidget.addControl(new LargeMapControl());
        mapWidget.panTo(LatLng.newInstance(-1, 25));
        
        mapWidget.addMapClickHandler(new MapClickHandler() {
			@Override
			public void onClick(MapClickEvent event) {
				onMapClick(event);
			}
		});
	}
    
    /**
     * Clears all existing content from the map
     */
    private void clearOverlays() {
    	if(mapWidget != null) {
    		mapWidget.clearOverlays();
    	}
        overlays.clear();
    }
    
	private void getBaseMaps() {
		GetBaseMaps getBaseMaps = new GetBaseMaps();
		
		dispatcher.execute(getBaseMaps, new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()), new AsyncCallback<BaseMapResult>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(BaseMapResult result) {
				if (result.getBaseMaps() == null) {
					// TODO: handle nullresult
				} else {
					baseMaps = result.getBaseMaps();
					mapReportElement.setBaseMapId(BaseMap.getDefaultMapId());
					setValue(mapReportElement);
				}
			}
		});
	}
	
    /**
     * Updates the size of the map and adds Overlays to reflect the content
     * of the current selected indicators
     */
    private void updateMapToContent() {
    	// Don't update when no layers are present
    	if (mapReportElement.getLayers().isEmpty()) {
    		isFirstLayerUpdate = true;
    		return;
    	}
    	// Prevent setting the extends for the MapWidget when more then 1 layer is added
		if (isFirstLayerUpdate && 
				mapReportElement.getLayers().size() > 0) {
			isFirstLayerUpdate = false;
		}
		
		Component maskingComponent = mapPage == null ? this : mapPage;
    	
    	dispatcher.execute(new GenerateElement<MapContent>(mapReportElement), new MaskingAsyncMonitor(maskingComponent, I18N.CONSTANTS.loadingMap()), new AsyncCallback<MapContent>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Load failed", caught.getMessage(), null);
			}

			@Override
			public void onSuccess(MapContent result) {
		        Log.debug("MapPreview: Received content, extents are = " + result.getExtents().toString());

		        mapModel = result;
		        
		        setBaseMap(result.getBaseMap());

		        layout();
		
		        statusWidget.setStatus(result.getUnmappedSites().size() + " " + I18N.CONSTANTS.siteLackCoordiantes(), null);
		
		        GcIconFactory iconFactory = new GcIconFactory();
		        iconFactory.primaryColor = "#0000FF";
		
		        putMarkersOnMap(result);
		        zoomToMarkers(result);
			}
		});
    }
    
	private void zoomToMarkers(MapContent result) {
		zoomToBounds(llBoundsForExtents(result.getExtents()));
	}
	
	private void putMarkersOnMap(MapContent result) {
		for (MapMarker marker : result.getMarkers()) {
            Icon icon = IconFactory.createIcon(marker);
            LatLng latLng = LatLng.newInstance(marker.getLat(), marker.getLng());

            MarkerOptions options = MarkerOptions.newInstance();
            options.setIcon(icon);
            options.setTitle(marker.getTitle());
            Marker overlay = new Marker(latLng, options);
            
            mapWidget.addOverlay(overlay);
            overlays.put(overlay, marker);
        }
	}
	
	/**
     * Handles the failure of the Google Maps API to load.
     */
	 private void handleApiLoadFailure() {
	        apiLoadFailed = true;

        if (this.getItemCount() == 0) {
            add(new Html(I18N.CONSTANTS.cannotLoadMap()));

            add(new Button(I18N.CONSTANTS.retry(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    createMapIfNeededAndUpdateMapContent();
                }
            }));
            
            layout();
        }
    }


	private void onMapClick(MapClickEvent event) {
		if(event.getOverlay() != null) {
			MapMarker marker = overlays.get(event.getOverlay());
			if (marker != null)
			{
				InfoWindowContent content = new InfoWindowContent(constructInfoWindowContent(marker));
				mapWidget.getInfoWindow().open((Marker)event.getOverlay(), content);
			}
		}
	}

	private String constructInfoWindowContent(MapMarker marker) {
		if (marker instanceof PieMapMarker) {
			PieMapMarker piemarker = (PieMapMarker)marker;
			
			// Create a list with all items with the value colored
			StringBuilder builder = new StringBuilder();
			
			// Start an html list
			builder.append("<ul>");

			// Add each slice of the pie as a listitem
			for (SliceValue slice : piemarker.getSlices()) {
				IndicatorDTO indicator = mapModel.getIndicatorById(slice.getIndicatorId());

				builder.append("<li>");
				builder.append("<b><span style=\"background-color:" + slice.getColor() + "\">");
				builder.append(slice.getValue());
				builder.append("</span></b> ");
				builder.append(indicator.getName());
			}

			// Close and return the html list
			builder.append("</ul>");
			return builder.toString();
			
		} else if (marker instanceof BubbleMapMarker) {
			BubbleMapMarker bubbleMarker = (BubbleMapMarker)marker;
			
			// Create a list with all items with the value colored
			StringBuilder builder = new StringBuilder();
			
			// Start an html list
			builder.append("<b>" + I18N.CONSTANTS.sum() + " " + marker.getTitle() + " </b><ul>");

			// Add each slice of the pie as a listitem
			for (Integer indicatorId : bubbleMarker.getIndicatorIds()) {
				IndicatorDTO indicator = mapModel.getIndicatorById(indicatorId);

				builder.append("<li>");
				builder.append(indicator.getName());
			}

			// Close and return the html list
			builder.append("</ul>");
			return builder.toString();
		} else if (marker instanceof IconMapMarker) {
			IndicatorDTO indicator = mapModel.getIndicatorById
										(((IconMapMarker) marker).getIndicatorId());
			return indicator.getName();
		}
		return null;
	}

	public void setMaster(MapPage mapPage) {
		this.mapPage = mapPage;
	}
}
