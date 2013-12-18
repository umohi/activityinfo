package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.core.client.ActivityInfoService;
import org.activityinfo.ui.core.client.model.DatabaseModel;
import org.activityinfo.ui.desktop.client.ClientFactory;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
    CellList<DatabaseModel> databaseList;
       
    public HomeView(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        this.databaseList = new CellList<DatabaseModel>(new DatabaseListCell(clientFactory));
        this.databaseList.setSelectionModel(new NoSelectionModel<DatabaseModel>());
        initWidget(uiBinder.createAndBindUi(this));

        DatabaseListProvider provider = new DatabaseListProvider(clientFactory.getService());
        provider.addDataDisplay(databaseList);
    }


}
