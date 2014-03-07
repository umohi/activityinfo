package org.activityinfo.ui.full.client.page.instance.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.page.instance.widgets.BreadCrumbBuilder;
import org.activityinfo.ui.full.client.widget.AsyncPanel;

/**
 * Provides a view for a FormClass instance
 */
public class FormView implements InstanceView {

    private final Widget rootElement;
    private final ResourceLocator resourceLocator;

    interface FormViewUiBinder extends UiBinder<HeaderPanel, FormView> {
    }

    private static FormViewUiBinder ourUiBinder = GWT.create(FormViewUiBinder.class);

    @UiField
    HeadingElement nameElement;

    @UiField
    Element breadCrumbElement;

    private BreadCrumbBuilder breadCrumb;

    @UiField
    AsyncPanel contentPanel;

    public FormView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        rootElement = ourUiBinder.createAndBindUi(this);
        breadCrumb = new BreadCrumbBuilder(resourceLocator, breadCrumbElement);
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