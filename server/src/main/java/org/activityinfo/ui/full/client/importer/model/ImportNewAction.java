package org.activityinfo.ui.full.client.importer.model;

import org.activityinfo.ui.full.client.i18n.I18N;

/**
 * Imports the column to a new Field
 */
public class ImportNewAction implements ColumnAction {

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.importNewColumnAction();
    }

    @Override
    public boolean isSingleColumn() {
        return false;
    }
}
