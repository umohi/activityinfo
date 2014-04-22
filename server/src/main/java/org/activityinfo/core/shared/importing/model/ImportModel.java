package org.activityinfo.core.shared.importing.model;

import com.google.common.collect.Maps;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceColumnAccessor;
import org.activityinfo.core.shared.importing.source.SourceTable;
import org.activityinfo.core.shared.importing.strategy.ColumnAccessor;
import org.activityinfo.core.shared.importing.strategy.TargetSiteId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A model which defines the mapping from an {@code SourceTable}
 * to a FormClass
 */
public class ImportModel {

    private SourceTable source;
    private FormTree formTree;


    private Map<SourceColumn, ColumnAction> columnActions = Maps.newHashMap();


    public ImportModel(FormTree formTree) {
        this.formTree = formTree;
    }

    public void setSource(SourceTable source) {
        this.source = source;
        this.columnActions = new HashMap<>();
    }

    public SourceTable getSource() {
        return source;
    }


    public void setColumnBinding(ColumnAction action, Integer columnIndex) {

        SourceColumn sourceColumn = source.getColumns().get(columnIndex);

        Iterator<Map.Entry<SourceColumn, ColumnAction>> it = columnActions.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(action)) {
                it.remove();
            }
        }
        columnActions.put(sourceColumn, action);
    }


    public FormTree getFormTree() {
        return formTree;
    }

    public ColumnAction getColumnAction(int columnIndex) {
        ColumnAction action = columnActions.get(columnIndex); // todo check ! map has key SourceColumn not integer
        return action;
    }

    public SourceColumn getSourceColumn(int columnIndex) {
        return source.getColumns().get(columnIndex);
    }

    public Map<SourceColumn, ColumnAction> getColumnActions() {
        return columnActions;
    }

    public Map<TargetSiteId, ColumnAccessor> getMappedColumns(Cuid fieldId) {
        Map<TargetSiteId, ColumnAccessor> mappings = Maps.newHashMap();
        for(Map.Entry<SourceColumn, ColumnAction> entry : columnActions.entrySet()) {
            if(entry.getValue() instanceof MapExistingAction) {
                MapExistingAction action = (MapExistingAction) entry.getValue();
                if(action.getTarget().getFieldId().equals(fieldId)) {
                    TargetSiteId site = action.getTarget().getSite();
                    ColumnAccessor column = new SourceColumnAccessor(entry.getKey());
                    mappings.put(site, column);
                }
            }
        }
        return mappings;
    }

    public void setColumnAction(int columnIndex, ColumnAction target) {
        columnActions.put(source.getColumns().get(columnIndex), target);
    }

    public ColumnAction getColumnAction(SourceColumn column) {
        return columnActions.get(column);
    }
}
