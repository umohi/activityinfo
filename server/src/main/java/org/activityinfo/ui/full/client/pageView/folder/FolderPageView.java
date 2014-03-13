package org.activityinfo.ui.full.client.pageView.folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.ParentCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.full.client.component.list.InstanceList;
import org.activityinfo.ui.full.client.page.instance.BreadCrumbBuilder;
import org.activityinfo.ui.full.client.pageView.InstancePageView;

import java.util.List;

/**
 * View for Folder instances
 */
public class FolderPageView implements InstancePageView {


    private FormInstance instance;
    private ResourceLocator resourceLocator;

    interface FolderViewUiBinder extends UiBinder<HTMLPanel, FolderPageView> {
    }

    private static FolderViewUiBinder ourUiBinder = GWT.create(FolderViewUiBinder.class);

    private final HTMLPanel rootElement;
    private final ScrollPanel scrollPanel;

    @UiField
    HeadingElement folderNameElement;

    @UiField
    Element folderDescriptionElement;

    @UiField
    Element breadCrumbElement;

    @UiField
    InstanceList instanceList;

    private BreadCrumbBuilder breadCrumb;

    public FolderPageView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        scrollPanel = new ScrollPanel(rootElement);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);
    }

    public void show(FormInstance folderInstance) {
        this.instance = folderInstance;
        folderNameElement.setInnerText(instance.getString(FolderClass.LABEL_FIELD_ID));
        folderDescriptionElement.setInnerText(instance.getString(FolderClass.DESCRIPTION_FIELD_ID));
        instanceList.show(queryChildren());
        breadCrumb.show(folderInstance);
    }

    private Promise<List<Projection>> queryChildren() {
        return resourceLocator.query(InstanceQuery
            .select(
                    ApplicationProperties.LABEL_PROPERTY,
                    ApplicationProperties.DESCRIPTION_PROPERTY)
            .where(ParentCriteria.isChildOf(instance.getId())).build());
    }

    @Override
    public Widget asWidget() {
        return scrollPanel;
    }

}