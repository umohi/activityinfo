package org.activityinfo.ui.desktop.client.home;

import org.activityinfo.ui.core.client.model.DatabaseItem;
import org.activityinfo.ui.core.client.places.DatabasePlace;

import com.bedatadriven.rebar.bootstrap.client.Bootstrap;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiRenderer;

public class DatabaseListCell extends AbstractCell<DatabaseItem> {

    private final PlaceHistoryMapper placeHistoryMapper;

    public DatabaseListCell(PlaceHistoryMapper placeHistoryMapper) {
        this.placeHistoryMapper = placeHistoryMapper;
    }

    interface MyUiRenderer extends UiRenderer {
        void render(SafeHtmlBuilder sb, String name, SafeUri link, Bootstrap b);
    }
    
    private static MyUiRenderer renderer = GWT.create(MyUiRenderer.class);

    @Override
    public void render(Context context, DatabaseItem value, SafeHtmlBuilder sb) {
        String link = placeHistoryMapper.getToken(new DatabasePlace(value.getId()));
        renderer.render(sb, value.getName(), UriUtils.fromTrustedString("#" + link), Bootstrap.INSTANCE);
    }
}
