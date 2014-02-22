package org.activityinfo.ui.full.client.importer.model;

import javax.annotation.Nonnull;

/**
 * Defines an action for a column in the ImportedTable
 */
public interface ColumnAction {

    @Nonnull
    String getLabel();

    /**
     *
     * @return true if this ColumnAction can be applied to a single column
     */
    boolean isSingleColumn();

}
