package org.activityinfo.ui.full.client.importer.ui.validation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.full.client.importer.model.ImportModel;

public class ValidationPage<T> extends Composite {

    private static ValidationPageUiBinder uiBinder = GWT
            .create(ValidationPageUiBinder.class);

    interface ValidationPageUiBinder extends UiBinder<Widget, ValidationPage> {
    }

    @UiField(provided = true)
    ValidationGrid<T> dataGrid;

    public ValidationPage(ImportModel<T> mapping) {

        dataGrid = new ValidationGrid<T>(mapping);

        initWidget(uiBinder.createAndBindUi(this));
    }

    public void refresh() {
        dataGrid.refreshRows();
    }
}
