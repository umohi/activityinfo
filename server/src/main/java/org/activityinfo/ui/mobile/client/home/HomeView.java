package org.activityinfo.ui.mobile.client.home;

import java.util.List;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.core.client.model.ModelList;
import org.activityinfo.ui.core.client.places.DatabasePlace;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.celllist.BasicCell;
import com.googlecode.mgwt.ui.client.widget.celllist.CellListWithHeader;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedEvent;
import com.googlecode.mgwt.ui.client.widget.celllist.CellSelectedHandler;

public class HomeView implements IsWidget {

    private LayoutPanel main;
    private HeaderPanel headerPanel;
    private HeaderButton forwardButton;
    private CellListWithHeader<DatabaseItem> cellList;
    private List<DatabaseItem> databases;

    public HomeView(final ClientFactory clientFactory) {
        main = new LayoutPanel();

        headerPanel = new HeaderPanel();
        headerPanel.setCenter("Databases");

        main.add(headerPanel);

        cellList = new CellListWithHeader<DatabaseItem>(new BasicCell<DatabaseItem>() {

                @Override
                public String getDisplayString(DatabaseItem model) {
                        return model.getName();
                }

                @Override
                public boolean canBeSelected(DatabaseItem model) {
                        return true;
                }
        });

        cellList.getCellList().setRound(true);
        clientFactory.getDatabaseIndex().get(new AsyncCallback<ModelList<DatabaseItem>>() {
            
            @Override
            public void onSuccess(ModelList<DatabaseItem> result) {
                cellList.getCellList().render(result.getItems());
            }
            
            @Override
            public void onFailure(Throwable caught) {
                
            }
        });
        
        cellList.getCellList().addCellSelectedHandler(new CellSelectedHandler() {
            
            @Override
            public void onCellSelected(CellSelectedEvent event) {
               DatabaseItem database = databases.get(event.getIndex());
               clientFactory.getPlaceController().goTo(new DatabasePlace(database.getId()));
            }
        });
        
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(cellList);
        scrollPanel.setScrollingEnabledX(false);
        main.add(scrollPanel);
    }
    
    @Override
    public Widget asWidget() {
        return main;
    }
}
