package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.core.client.model.ModelList;
import org.activityinfo.ui.desktop.client.widget.editor.ModalDialog;
import org.activityinfo.ui.desktop.client.widget.resources.HasResource;
import org.activityinfo.ui.desktop.client.widget.resources.ListResourcePanel;
import org.activityinfo.ui.desktop.client.widget.resources.SimpleCellList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite {

    private final ClientFactory clientFactory;
    
    private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);
    
    interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
    }

    @UiField(provided=true)
    Widget databaseList;
    
    @UiField Anchor newDatabaseLink;
       
    public HomeView(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        
        HasResource<ModelList<DatabaseItem>> simpleCellList = new SimpleCellList<DatabaseItem>(
            new DatabaseListCell(clientFactory.getPlaceHistoryMapper()));
        ListResourcePanel<DatabaseItem> panel = ListResourcePanel.create(clientFactory.getDatabaseIndex(), simpleCellList);
        
        this.databaseList = panel;
        
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @UiHandler("newDatabaseLink")
    public void onNewDatabaseClick(ClickEvent event) {
    	ModalDialog dialog = new ModalDialog();
    	dialog.show();
    }
}
