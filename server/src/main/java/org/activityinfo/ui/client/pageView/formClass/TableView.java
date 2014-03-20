package org.activityinfo.ui.client.pageView.formClass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.activityinfo.ui.client.component.table.InstanceTable;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displays the this classes' FormInstances in a table format
 */
public class TableView implements IsWidget, RequiresResize {

    private static final int DEFAULT_MAX_COLUMN_COUNT = 5;
    private static final Logger LOGGER = Logger.getLogger(TableView.class.getName());

    private final HTMLPanel panel;

    @UiField
    DivElement emRuler;

    @UiField
    Element columnAlert;

    @UiField(provided = true)
    InstanceTable table;

    interface TableViewUiBinder extends UiBinder<HTMLPanel, TableView> {
    }

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);

    public TableView(ResourceLocator resourceLocator) {
        table = new InstanceTable(resourceLocator);
        panel = ourUiBinder.createAndBindUi(this);
    }

    public void setCriteria(Criteria criteria) {
        table.setCriteria(criteria);
    }

    public void setColumns(final List<FieldColumn> columns) {
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                doSetColumns(columns);
            }
        });
    }

    private void doSetColumns(List<FieldColumn> columns) {
        if(columns.size() <= getMaxNumberOfColumns()) {
            table.setColumns(columns);
            columnAlert.getStyle().setDisplay(Style.Display.NONE);
        } else {
            table.setColumns(columns.subList(0, getMaxNumberOfColumns()));
            columnAlert.getStyle().clearDisplay();
        }
    }

    public int getMaxNumberOfColumns() {
        double emSizeInPixels = ((double)emRuler.getOffsetWidth()) / 100d;

        LOGGER.log(Level.FINE, "emSizeInPixels = " + emSizeInPixels);

        double columnWidthInPixels = InstanceTable.COLUMN_WIDTH * emSizeInPixels;

        int columnLimit = (int) Math.floor(panel.getElement().getClientWidth() / columnWidthInPixels);
        LOGGER.log(Level.FINE, "columnLimit = " + columnLimit);
        if (columnLimit <= 0) { // fallback : yuriyz: todo check calculations above
            columnLimit = DEFAULT_MAX_COLUMN_COUNT;
        }
        return columnLimit;
    }

    public InstanceTable getTable() {
        return table;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void onResize() {

    }
}