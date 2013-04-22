package org.activityinfo.geoadmin;

import javax.swing.table.AbstractTableModel;

import org.activityinfo.geoadmin.model.AdminEntity;

/**
 * A TableModel that combines a list of imported features along with a column
 * where the user can choose the parent in the existing hierarchy.
 * 
 */
public class ImportTableModel extends AbstractTableModel {

    public static final int PARENT_COLUMN = 0;
    public static final int STATUS_COLUMN = 1;

    public static final int NUM_EXTRA_COLUMNS = 1;

    private ImportSource source;
    private AdminEntity[] parents;

    public ImportTableModel(ImportSource source) {
        this.source = source;
        this.parents = new AdminEntity[source.getFeatureCount()];
    }

    @Override
    public int getColumnCount() {
        return source.getAttributes().size() + NUM_EXTRA_COLUMNS;
    }

    @Override
    public int getRowCount() {
        return source.getFeatures().size();
    }

    public ImportFeature getFeatureAt(int rowIndex) {
        return source.getFeatures().get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        switch (colIndex) {
        case 0:
            return parents[rowIndex];
        default:
            return source.getFeatures().get(rowIndex).getAttributeValue(colIndex - 1);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case PARENT_COLUMN:
            return AdminEntity.class;
        default:
            return source.getAttributes().get(columnIndex - 1).getType().getBinding();
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case PARENT_COLUMN:
            return "PARENT";
        default:
            return source.getAttributes().get(columnIndex - NUM_EXTRA_COLUMNS).getName().getLocalPart();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case PARENT_COLUMN:
            return true;
        default:
            return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == PARENT_COLUMN) {
            parents[rowIndex] = (AdminEntity) aValue;
            fireRowChanged(rowIndex);
        }
    }

    private void fireRowChanged(int rowIndex) {
        for (int i = 0; i != getColumnCount(); ++i) {
            fireTableCellUpdated(rowIndex, i);
        }
    }

    public AdminEntity getParent(int featureIndex) {
        return parents[featureIndex];
    }
}
