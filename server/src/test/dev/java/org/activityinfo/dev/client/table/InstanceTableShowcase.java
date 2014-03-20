package org.activityinfo.dev.client.table;

import com.google.gwt.user.client.ui.FlowPanel;
import org.activityinfo.dev.client.DevResourceLocatorAdaptor;
import org.activityinfo.ui.client.pageView.formClass.TableView;
import org.activityinfo.ui.client.style.table.CellTableResources;

public class InstanceTableShowcase extends FlowPanel {

    private final TableView tableView = new TableView(new DevResourceLocatorAdaptor());

    public InstanceTableShowcase() {
        CellTableResources.INSTANCE.cellTableStyle().ensureInjected();
        add(tableView);
    }
}