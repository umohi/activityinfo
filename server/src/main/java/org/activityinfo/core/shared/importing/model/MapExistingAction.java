package org.activityinfo.core.shared.importing.model;

import org.activityinfo.core.shared.importing.strategy.ImportTarget;

/**
 * ColumnAction which maps the input SourceColumn to an existing Field
 */
public class MapExistingAction extends ColumnAction {
    private ImportTarget target;

    public MapExistingAction(ImportTarget target) {
        this.target = target;
    }

    public ImportTarget getTarget() {
        return target;
    }
}
