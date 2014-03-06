package org.activityinfo.ui.full.client.page.home.list;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.ResourceLocator;

/**
 * Created by alex on 3/4/14.
 */
public class ItemListPanel implements IsWidget {

    private HTML html;

    private final ResourceLocator resourceLocator;

    public ItemListPanel(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }



    @Override
    public Widget asWidget() {
        return html;
    }
}
