package org.activityinfo.ui.desktop.client.widget.resources;

import org.activityinfo.ui.core.client.resources.Resource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ResourcePanel<T> extends Composite {

    private static final int LOADING = 0;
    private static final int LOADED = 1;
    private static final int ERROR = 2;
    
    private DeckPanel deckPanel;
    private Widget loadingWidget;
    private FlowPanel loadedPanel;
    private HasResource<T> resourceWidget;
    private RefreshWidget refreshWidget;
    private LoadingErrorWidget loadingErrorWidget;
    
    private Resource<T> index;

    public ResourcePanel(Resource<T> index, HasResource<T> resourceWidget) {
        this.index = index;
        this.resourceWidget = resourceWidget;
        
        loadingWidget = new Label("Loading...");
        refreshWidget = new RefreshWidget();
        refreshWidget.setVisible(false);
        loadingErrorWidget = new LoadingErrorWidget();
     
        loadedPanel = new FlowPanel();
        loadedPanel.add(resourceWidget);
        loadedPanel.add(refreshWidget);
        
        deckPanel = new DeckPanel();
        deckPanel.add(loadingWidget);
        deckPanel.add(loadedPanel);
        deckPanel.add(loadingErrorWidget);
        
        initWidget(deckPanel);
        
        load();
    }
    
    private void load() {
        deckPanel.showWidget(LOADING);
        index.get(new AsyncCallback<T>() {

            @Override
            public void onFailure(Throwable caught) {
                loadingErrorWidget.showError(caught);
                deckPanel.showWidget(ERROR);
            }

            @Override
            public void onSuccess(T resource) {
                resourceWidget.showResource(resource);
                deckPanel.showWidget(LOADED);
            }
        });
    }
}
