package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ProjectionKeyProvider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.style.table.DataGridResources;
import org.activityinfo.ui.client.widget.HasScrollAncestor;
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

    public static final String FALLBACK_TABLE_HEIGHT_PX = "500px";


    // container is used to calculate table height
//    private final ResizeLayoutPanel containerPanel = new ResizeLayoutPanel();
    private final ResourceLocator resourceLocator;

    private final ListDataProvider<Projection> tableDataProvider = new ListDataProvider<>();
    private final DataGrid<Projection> table;
    private final TableLoadingIndicator loadingIndicator;
    private final MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());

    private HasScrollAncestor hasScrollAncestor;
    private Set<FieldPath> fields = Sets.newHashSet();
    private Criteria criteria;
    private int tableHeightReduction;

    public InstanceTable(ResourceLocator resourceLocator, HasScrollAncestor hasScrollAncestor) {
        this.resourceLocator = resourceLocator;
        this.hasScrollAncestor = hasScrollAncestor;
        DataGridResources.INSTANCE.dataGridStyle().ensureInjected();

        table = new DataGrid<>(50, DataGridResources.INSTANCE);
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);
        table.setSkipRowHoverStyleUpdate(true);

        // Set the table to fixed width: we will provide explicit
        // column widths
        table.setWidth("100%");
        table.setHeight(FALLBACK_TABLE_HEIGHT_PX);
        table.setSelectionModel(selectionModel);
        table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent event) {
                onRangeChanged(event);
            }
        });
        tableDataProvider.addDataDisplay(table);

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
        for (FieldColumn column : columns) {
            table.addColumn(column, column.getHeader());
            fields.addAll(column.getFieldPaths());
        }

        reload();
    }

    private void removeAllColumns() {
        while (table.getColumnCount() > 0) {
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
                tableDataProvider.setList(result);
            }
        });
        recalculateTableHeight();
    }

    public MultiSelectionModel<Projection> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public Widget asWidget() {
        return table;
    }

    public DataGrid<Projection> getTable() {
        return table;
    }

    private void onRangeChanged(RangeChangeEvent event) {
        LOGGER.log(Level.INFO, "Instance Table Range Change: " +
                "start = " + event.getNewRange().getStart() +
                ", length = " + event.getNewRange().getLength());


        reload();
    }

    public int getTableHeightReduction() {
        return tableHeightReduction;
    }

    public void setTableHeightReduction(int tableHeightReduction) {
        this.tableHeightReduction = tableHeightReduction;
    }

    public void recalculateTableHeight(int tableHeightReduction) {
        setTableHeightReduction(tableHeightReduction);
        recalculateTableHeight();
    }

    public void recalculateTableHeight() {
        if (hasScrollAncestor != null && hasScrollAncestor.getScrollAncestor() != null) {
            final int offsetHeight = hasScrollAncestor.getScrollAncestor().getOffsetHeight();
            if (offsetHeight > 0) {
                // header and links and other ancestors stuff to which we don't have references
                final int ancestorsStuffHeight = 175; // ugly magic number - need better way to calculate it
                final int height = offsetHeight - ancestorsStuffHeight - tableHeightReduction;
                if (height > 0) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            table.setHeight(height + "px");
                            improveTableHeightIfScrollAppears(height);
                        }
                    });
                }
            }
        }
    }

    private void improveTableHeightIfScrollAppears(int height) {
        final int verticalScrollPosition = hasScrollAncestor.getScrollAncestor().getVerticalScrollPosition();
        if (verticalScrollPosition > 0) {
            final int newHeight = height - 10;
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    table.setHeight(newHeight + "px");
                    improveTableHeightIfScrollAppears(newHeight);
                }
            });
        }
    }
}
