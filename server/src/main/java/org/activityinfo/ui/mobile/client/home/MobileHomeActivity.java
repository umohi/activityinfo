package org.activityinfo.ui.mobile.client.home;

import org.activityinfo.ui.core.client.ClientFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class MobileHomeActivity extends AbstractActivity {

    private final ClientFactory clientFactory;
    
    public MobileHomeActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
    
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(new HomeView(clientFactory));
    }

}
