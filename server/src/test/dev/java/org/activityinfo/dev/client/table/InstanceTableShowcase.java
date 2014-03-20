package org.activityinfo.dev.client.table;

import com.google.gwt.user.client.ui.FlowPanel;
import org.activityinfo.dev.client.DevResourceLocatorAdaptor;
import org.activityinfo.ui.client.pageView.formClass.InstanceTableView;
import org.activityinfo.ui.client.style.table.CellTableResources;

public class InstanceTableShowcase extends FlowPanel {

    private final InstanceTableView tableView = new InstanceTableView(new DevResourceLocatorAdaptor());

    public InstanceTableShowcase() {
        CellTableResources.INSTANCE.cellTableStyle().ensureInjected();
        add(tableView);
    }
}