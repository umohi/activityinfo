package org.activityinfo.ui.desktop.client.database;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseView extends Composite {

    private static DatabaseViewUiBinder uiBinder = GWT.create(DatabaseViewUiBinder.class);

    interface DatabaseViewUiBinder extends UiBinder<Widget, DatabaseView> {
    }

    public DatabaseView() {
        initWidget(uiBinder.createAndBindUi(this));

    }

}
