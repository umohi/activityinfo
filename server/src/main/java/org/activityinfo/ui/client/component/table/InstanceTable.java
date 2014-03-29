package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ProjectionKeyProvider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.style.table.CellTableResources;
import org.activityinfo.ui.client.widget.loading.LoadingState;
import org.activityinfo.ui.client.widget.loading.TableLoadingIndicator;

import java.util.List;
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
    private final TableLoadingIndicator loadingIndicator;
    private final MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());

    private Set<FieldPath> fields = Sets.newHashSet();
    private Criteria criteria;

    public InstanceTable(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        CellTableResources.INSTANCE.cellTableStyle().ensureInjected();

        table = new CellTable<>(50, CellTableResources.INSTANCE);
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);
        table.setSkipRowHoverStyleUpdate(true);

        // Set the table to fixed width: we will provide explicit
        // column widths
        table.setWidth("100%", true);
        table.setSelectionModel(selectionModel);
        table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent event) {
                onRangeChanged(event);
            }
        });

        // Create our loading indicator which can also show failure
        loadingIndicator = new TableLoadingIndicator();
        loadingIndicator.getRetryButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                reload();
            }
        });
        table.setLoadingIndicator(loadingIndicator.asWidget());
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public void setColumns(List<FieldColumn> columns) {
        removeAllColumns();
        for(FieldColumn column : columns) {
            table.addColumn(column, column.getHeader());
            fields.addAll(column.getFieldPaths());
        }

        reload();
    }

    private void removeAllColumns() {
        while(table.getColumnCount() > 0) {
            table.removeColumn(0);
        }
    }

    public void reload() {
        loadingIndicator.onLoadingStateChanged(LoadingState.LOADING, null);

        InstanceQuery query = new InstanceQuery(Lists.newArrayList(fields), criteria);
        resourceLocator.query(query).then(new AsyncCallback<List<Projection>>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, "Failed to load instances. Criteria = " +
                        criteria + ", fields = " + fields, caught);
                loadingIndicator.onLoadingStateChanged(LoadingState.FAILED, caught);
            }

            @Override
            public void onSuccess(List<Projection> result) {
                table.setRowData(result);
            }
        });
    }

    public MultiSelectionModel<Projection> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public Widget asWidget() {
        return table;
    }

    private void onRangeChanged(RangeChangeEvent event) {
        LOGGER.log(Level.INFO, "Instance Table Range Change: " +
                "start = " + event.getNewRange().getStart() +
                ", length = " + event.getNewRange().getLength());


        reload();
    }
}
