package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ProjectionKeyProvider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.style.table.CellTableResources;
import org.activityinfo.ui.client.widget.async.FailureWidget;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable component to display Instances in a table
 */
public class InstanceTable implements IsWidget {

    private static final Logger LOGGER = Logger.getLogger(InstanceTable.class.getName());

    /**
     * The default column width, in {@code em}
     */
    public static final int COLUMN_WIDTH = 10;


    private final ResourceLocator resourceLocator;
    private final CellTable<Projection> table;

    private Set<FieldPath> fields = Sets.newHashSet();

    private Criteria criteria;

    public InstanceTable(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        CellTableResources.INSTANCE.cellTableStyle().ensureInjected();

        table = new CellTable<>(50, CellTableResources.INSTANCE);
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);

        // Set the table to fixed width: we will provide explicit
        // column widths
        table.setWidth("100%", true);

        MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());
        table.setSelectionModel(selectionModel);

        table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent event) {
                onRangeChanged(event);
            }
        });
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public void setColumns(List<FieldColumn> columns) {
        while(table.getColumnCount() > 0) {
            table.removeColumn(0);
        }
        for(FieldColumn column : columns) {
            table.addColumn(column, column.getHeader());
            fields.addAll(column.getFieldPaths());
        }
        loadData();
    }

    @Override
    public Widget asWidget() {
        return table;
    }

    private void onRangeChanged(RangeChangeEvent event) {
        LOGGER.log(Level.INFO, "Instance Table Range Change: " +
                "start = " + event.getNewRange().getStart() +
                "length = " + event.getNewRange().getLength());


        loadData();

    }

    private void loadData() {
        InstanceQuery query = new InstanceQuery(Lists.newArrayList(fields), criteria);
        resourceLocator.query(query).then(new AsyncCallback<List<Projection>>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, "Exception loading instance table", caught);
            }

            @Override
            public void onSuccess(List<Projection> result) {
                table.setRowData(result);
            }
        });
    }

}
