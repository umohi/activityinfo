package org.activityinfo.ui.full.client.page.instance.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.criteria.ParentCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.system.FolderClass;

import java.util.List;

/**
 * View for Folder instances
 */
public class FolderView implements IsWidget {


    private FormInstance instance;
    private ResourceLocator resourceLocator;

    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderView> {
    }

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    private final HTMLPanel rootElement;

    @UiField
    HeadingElement folderNameElement;

    @UiField
    Element folderDescriptionElement;

    public FolderView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
    }

    public void show(FormInstance folderInstance) {
        this.instance = folderInstance;
        folderNameElement.setInnerText(instance.getString(FolderClass.LABEL_FIELD_ID));
        folderDescriptionElement.setInnerText(instance.getString(FolderClass.DESCRIPTION_FIELD_ID));

    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

}