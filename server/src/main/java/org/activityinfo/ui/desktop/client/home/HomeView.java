package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.data.DatabaseListProvider;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.desktop.client.widget.list.AsyncCellList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;

public class HomeView extends Composite {

    private final ClientFactory clientFactory;
    
    private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);
    
    interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
    }

    @UiField(provided=true)
    Widget databaseList;
       
    public HomeView(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        
        CellList<DatabaseItem> cellList = new CellList<DatabaseItem>(new DatabaseListCell(clientFactory));
        cellList.setSelectionModel(new NoSelectionModel<DatabaseItem>());
        
        AsyncCellList<DatabaseItem> asyncList = new AsyncCellList<DatabaseItem>(clientFactory.getDatabaseIndex(), cellList);
        
        this.databaseList = asyncList;
        
        initWidget(uiBinder.createAndBindUi(this));
    }
}
