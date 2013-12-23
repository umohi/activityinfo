package org.activityinfo.ui.desktop.client.database;

import org.activityinfo.ui.core.client.model.SchemaModel;
import org.activityinfo.ui.desktop.client.widget.list.HasResource;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseView extends Composite implements HasResource<SchemaModel> {

    private static DatabaseViewUiBinder uiBinder = GWT.create(DatabaseViewUiBinder.class);

    interface DatabaseViewUiBinder extends UiBinder<Widget, DatabaseView> {
    }

    @UiField Heading databaseHeading;
    
    public DatabaseView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void showResource(SchemaModel resource) {
        databaseHeading.setText(resource.getName());
    }
}
