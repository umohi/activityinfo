package org.activityinfo.ui.full.client.page.home.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.system.FolderClass;

/**
 * Created by alex on 3/4/14.
 */
public class FolderView {
    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderView> {
    }

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    @UiField
    HeadingElement folderNameElement;

    @UiField
    Element folderDescriptionElement;

    public FolderView(FormInstance instance) {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);

        folderNameElement.setInnerText(instance.getString(FolderClass.LABEL_FIELD_ID));
        folderDescriptionElement.setInnerText(instance.getString(FolderClass.DESCRIPTION_FIELD_ID));


    }
}