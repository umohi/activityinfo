package org.activityinfo.ui.mobile.client.home;

import java.util.List;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.model.DatabaseModel;
import org.activityinfo.ui.core.client.places.DatabasePlace;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

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
    private CellListWithHeader<DatabaseModel> cellList;
    private List<DatabaseModel> databases;

    public HomeView(final ClientFactory clientFactory) {
        main = new LayoutPanel();

        headerPanel = new HeaderPanel();
        headerPanel.setCenter("Databases");

        main.add(headerPanel);

        cellList = new CellListWithHeader<DatabaseModel>(new BasicCell<DatabaseModel>() {

                @Override
                public String getDisplayString(DatabaseModel model) {
                        return model.getName();
                }

                @Override
                public boolean canBeSelected(DatabaseModel model) {
                        return true;
                }
        });

        cellList.getCellList().setRound(true);

        clientFactory.getService().getDatabases(new MethodCallback<List<DatabaseModel>>() {
            
            @Override
            public void onSuccess(Method method, List<DatabaseModel> response) {
                databases = response;
                cellList.getCellList().render(response);
            }
            
            @Override
            public void onFailure(Method method, Throwable exception) {
                // TODO Auto-generated method stub
                
            }
        });
        
        cellList.getCellList().addCellSelectedHandler(new CellSelectedHandler() {
            
            @Override
            public void onCellSelected(CellSelectedEvent event) {
               DatabaseModel database = databases.get(event.getIndex());
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
