package org.activityinfo.ui.desktop.client.widget.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoadingErrorWidget extends Composite {

    private static LoadingErrorWidgetUiBinder uiBinder = GWT.create(LoadingErrorWidgetUiBinder.class);

    interface LoadingErrorWidgetUiBinder extends UiBinder<Widget, LoadingErrorWidget> {
    }

    public LoadingErrorWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void showError(Throwable caught) {
        
    }

}
