package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.core.client.ClientFactory;
import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.core.client.places.DatabasePlace;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiRenderer;

public class DatabaseListCell extends AbstractCell<DatabaseItem> {

    private final ClientFactory clientFactory;
    
    public DatabaseListCell(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    interface MyUiRenderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name, SafeUri link);
    }
    
    private static MyUiRenderer renderer = GWT.create(MyUiRenderer.class);

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, DatabaseItem value, SafeHtmlBuilder sb) {
        String link = clientFactory.getPlaceHistoryMapper().getToken(new DatabasePlace(value.getId()));
        renderer.render(sb, value.getName(), UriUtils.fromTrustedString("#" + link));
        
    }

}
