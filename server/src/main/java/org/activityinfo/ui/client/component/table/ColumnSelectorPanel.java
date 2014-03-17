package org.activityinfo.ui.client.component.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Notifies the user if it is not possible to fit all
 */
public class ColumnSelectorPanel {
    interface ColumnSelectorUiBinder extends UiBinder<HTMLPanel, ColumnSelectorPanel> {
    }

    private static ColumnSelectorUiBinder ourUiBinder = GWT.create(ColumnSelectorUiBinder.class);

    public ColumnSelectorPanel() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);

    }
}