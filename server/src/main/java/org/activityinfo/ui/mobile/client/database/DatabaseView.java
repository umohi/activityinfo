package org.activityinfo.ui.mobile.client.database;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class DatabaseView implements IsWidget {

    private LayoutPanel main;
    private HeaderPanel headerPanel;

    public DatabaseView() {
        main = new LayoutPanel();

        headerPanel = new HeaderPanel();
        headerPanel.setCenter("Tada! A Database");

        main.add(headerPanel);
    }
    
    @Override
    public Widget asWidget() {
        return main;
    }
}
