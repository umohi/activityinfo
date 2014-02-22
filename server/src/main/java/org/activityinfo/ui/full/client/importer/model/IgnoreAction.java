package org.activityinfo.ui.full.client.importer.model;

import org.activityinfo.ui.full.client.i18n.I18N;

/**
 * Ignores an imported column
 */
public class IgnoreAction implements ColumnAction {
    @Override
    public String getLabel() {
        return I18N.CONSTANTS.ignoreColumnAction();
    }

    @Override
    public boolean isSingleColumn() {
        return false;
    }
}
