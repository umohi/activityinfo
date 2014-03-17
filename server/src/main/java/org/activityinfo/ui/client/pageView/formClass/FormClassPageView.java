package org.activityinfo.ui.client.pageView.formClass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.page.instance.BreadCrumbBuilder;
import org.activityinfo.ui.client.pageView.IconStyleProvider;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.LoadingPanel;

/**
 * Provides a view for a FormClass instance
 */
public class FormClassPageView implements InstancePageView {

    private final Widget rootElement;
    private final ResourceLocator resourceLocator;

    interface FormViewUiBinder extends UiBinder<HTMLPanel, FormClassPageView> {
    }

    private static FormViewUiBinder ourUiBinder = GWT.create(FormViewUiBinder.class);

    @UiField
    Element nameElement;

    @UiField
    Element pageIcon;

    @UiField
    Element breadCrumbElement;

    private BreadCrumbBuilder breadCrumb;

    @UiField
    LoadingPanel<FormTree> contentPanel;

    public FormClassPageView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);

        Icons.INSTANCE.ensureInjected();
    }

    public Promise<Void> show(FormInstance instance) {
        Cuid classId = instance.getId();

        nameElement.setInnerText(instance.getString(FormClass.LABEL_FIELD_ID));
        pageIcon.setClassName(IconStyleProvider.getIconStyleForFormClass(instance.getId()));

        breadCrumb.show(instance);

        contentPanel.setDisplayWidget(new TablePresenter(resourceLocator));
        return contentPanel.show(new AsyncFormTreeBuilder(resourceLocator), classId);
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}