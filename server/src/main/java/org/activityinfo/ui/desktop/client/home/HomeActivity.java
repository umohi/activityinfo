package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.desktop.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class HomeActivity extends AbstractActivity {

    private final ClientFactory clientFactory;
    
    public HomeActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
    
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(new HomeView(clientFactory));
    }
}
