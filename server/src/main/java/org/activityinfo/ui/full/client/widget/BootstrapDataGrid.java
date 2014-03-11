package org.activityinfo.ui.full.client.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * Simple subclass of {@link com.google.gwt.user.cellview.client.DataGrid} that adds
 * Bootstrap Styles
 */
public class BootstrapDataGrid<T> extends DataGrid<T> {

    private static BootstrapResources RESOURCES = GWT.create(BootstrapResources.class);

    public BootstrapDataGrid(int pageSize) {
        super(pageSize, RESOURCES);

		Element headerTable = getTableHeadElement().getParentElement();
		headerTable.addClassName("datagrid");

        Element bodyTable = getTableBodyElement().getParentElement();
        bodyTable.addClassName("datagrid");
    }

    @Override
    public TableSectionElement getTableHeadElement() {
        return super.getTableHeadElement();
    }

    public interface BootstrapResources extends Resources {

        @Source("BootstrapDataGrid.css")
        Style dataGridStyle();

    }
}
