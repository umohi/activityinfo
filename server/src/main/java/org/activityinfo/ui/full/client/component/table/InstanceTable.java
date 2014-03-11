package org.activityinfo.ui.full.client.component.table;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.MultiSelectionModel;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.ProjectionKeyProvider;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.api2.shared.function.Functions2;
import org.activityinfo.ui.full.client.style.DataGridResources;

import java.util.List;
import java.util.Map;

/**
 * Reusable component to display Instances in a table
 */
public class InstanceTable implements IsWidget {

    private final FormTree formTree;

    private final DataGrid<Projection> grid;
    private final ResourceLocator resourceLocator;
    private Map<Cuid, FieldColumn> columns = Maps.newHashMap();


    public InstanceTable(ResourceLocator resourceLocator, FormTree formTree) {
        this.formTree = formTree;
        this.grid = new DataGrid<>(50, DataGridResources.INSTANCE);
        this.grid.setSkipRowHoverCheck(true);
        this.grid.setSkipRowHoverFloatElementCheck(true);


        MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());

        this.grid.setSelectionModel(selectionModel);

        this.resourceLocator = resourceLocator;

        addColumns(formTree.getRoot());

        fetchRows();
    }

    private void fetchRows() {
        List<FieldPath> paths = Lists.newArrayList();
        for(FieldColumn col : columns.values()) {
            paths.addAll(col.getFieldPaths());
        }

        resourceLocator
        .query(new InstanceQuery(paths, new ClassCriteria(formTree.getRootFormClass().getId())))
        .then(new AsyncCallback<List<Projection>>() {
            @Override
            public void onFailure(Throwable caught) {
                grid.setLoadingIndicator(new HTML("Error: " + caught));
            }

            @Override
            public void onSuccess(List<Projection> result) {
                grid.setRowData(result);
                grid.setRowCount(result.size());
            }
        });
    }

    private void addColumns(FormTree.Node root) {
        for(FormTree.Node child : root.getChildren()) {
            if(child.isReference()) {
                addColumns(child);
            } else {
                if(columns.containsKey(child.getFieldId())) {
                    columns.get(child.getFieldId()).addFieldPath(child.getPath());
                } else {
                    FieldColumn col = new FieldColumn(child.getPath());
                    columns.put(child.getFieldId(), col);
                    grid.addColumn(col, header(child));
                }
            }
        }
    }

    private String header(FormTree.Node child) {
        if(child.getPath().isNested()) {
            return child.getFormClass().getLabel().getValue() + " " + child.getField().getLabel().getValue();
        } else {
            return child.getField().getLabel().getValue();
        }
    }

    @Override
    public Widget asWidget() {
        return grid;
    }

    public static Function<FormTree, IsWidget> constructor(final ResourceLocator resourceLocator) {
        return new Function<FormTree, IsWidget>() {
            @Override
            public IsWidget apply(FormTree formTree) {
                return new InstanceTable(resourceLocator, formTree);
            }
        };
    }

    public static Function<Void, Promise<IsWidget>> creator(ResourceLocator resourceLocator, Cuid formClassId) {

        Function<Cuid, Promise<FormTree>> fetch = new AsyncFormTreeBuilder(resourceLocator);
        Function<Promise<FormTree>, Promise<IsWidget>> liftedConstructor = Promise.fmap(constructor(resourceLocator));

        Function<Cuid, Promise<IsWidget>> composed = Functions.compose(liftedConstructor, fetch);
        return Functions2.closeOver(composed, formClassId);
    }
}
