package org.activityinfo.ui.client.pageView.formClass;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.ui.client.component.table.InstanceTable;
import org.activityinfo.ui.client.page.instance.BreadCrumbBuilder;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.AsyncPanel;

/**
 * Provides a view for a FormClass instance
 */
public class FormClassPageView implements InstancePageView {

    private final Widget rootElement;
    private final ResourceLocator resourceLocator;

    interface FormViewUiBinder extends UiBinder<HeaderPanel, FormClassPageView> {
    }

    private static FormViewUiBinder ourUiBinder = GWT.create(FormViewUiBinder.class);

    @UiField
    HeadingElement nameElement;

    @UiField
    Element breadCrumbElement;

    private BreadCrumbBuilder breadCrumb;

    @UiField
    AsyncPanel contentPanel;

    public FormClassPageView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);

        Icons.INSTANCE.ensureInjected();

    }

    public void show(FormInstance instance) {
        nameElement.setInnerText(instance.getString(FormClass.LABEL_FIELD_ID));
        breadCrumb.show(instance);
        contentPanel.setWidget(InstanceTable.creator(resourceLocator, instance.getId()));
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}