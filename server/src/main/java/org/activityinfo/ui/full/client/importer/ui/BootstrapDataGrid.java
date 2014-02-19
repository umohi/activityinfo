package org.activityinfo.ui.full.client.importer.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.DataGrid;

public class BootstrapDataGrid<T> extends DataGrid<T> {

    private static BootstrapResources RESOURCES = GWT.create(BootstrapResources.class);

    public BootstrapDataGrid(int pageSize) {
        super(pageSize, RESOURCES);

//		Element headerTable = getTableHeadElement().getParentElement();
//		headerTable.addClassName("table");
//		headerTable.addClassName("table-bordered");
//		headerTable.addClassName("table-condensed");

        Element bodyTable = getTableBodyElement().getParentElement();
        bodyTable.addClassName("table");
        bodyTable.addClassName("table-bordered");
        bodyTable.addClassName("table-condensed");

    }


    public interface BootstrapResources extends Resources {

        @Source("BootstrapDataGrid.css")
        Style dataGridStyle();

    }
}
