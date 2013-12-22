package org.activityinfo.ui.desktop.client.widget.list;

import org.activityinfo.ui.core.client.data.IndexResult;
import org.activityinfo.ui.core.client.data.ResourceIndex;

import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AsyncCellList<T> extends Composite {

    private static final int LOADING = 0;
    private static final int LOADED = 1;
    private static final int ERROR = 2;
    
    private DeckPanel deckPanel;
    private Widget loadingWidget;
    private FlowPanel loadedPanel;
    private CellList<T> cellList;
    private RefreshWidget refreshWidget;
    private LoadingErrorWidget loadingErrorWidget;
    
    private ResourceIndex<T> index;

    public AsyncCellList(ResourceIndex<T> index, CellList<T> cellList) {
        this.index = index;
        this.cellList = cellList;
        
        loadingWidget = new Label("Loading...");
        refreshWidget = new RefreshWidget();
        refreshWidget.setVisible(false);
        loadingErrorWidget = new LoadingErrorWidget();
     
        loadedPanel = new FlowPanel();
        loadedPanel.add(cellList);
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
        index.get(new AsyncCallback<IndexResult<T>>() {

            @Override
            public void onFailure(Throwable caught) {
                loadingErrorWidget.showError(caught);
                deckPanel.showWidget(ERROR);
            }

            @Override
            public void onSuccess(IndexResult<T> result) {
                cellList.setRowData(result.getItems());
                deckPanel.showWidget(LOADED);
            }
        });
    }
}
