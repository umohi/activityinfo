package org.activityinfo.ui.desktop.client.widget.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

public class RefreshWidget extends Composite {

    private static RefreshWidgetUiBinder uiBinder = GWT.create(RefreshWidgetUiBinder.class);

    interface RefreshWidgetUiBinder extends UiBinder<Widget, RefreshWidget> {
    }
    
    @UiField InlineHyperlink refreshLink;

    public RefreshWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    public HasClickHandlers getRefreshLink() {
        return refreshLink;
    }
}
