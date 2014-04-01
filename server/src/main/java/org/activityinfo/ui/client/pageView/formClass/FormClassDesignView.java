package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.component.form.FormPanel;
import org.activityinfo.ui.client.pageView.InstancePageView;

import javax.annotation.Nullable;

public class FormClassDesignView implements InstancePageView {

    private ResourceLocator resourceLocator;
    private FlowPanel container;
    private FormPanel formPanel;

    public FormClassDesignView(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.container = new FlowPanel();

    }

    @Override
    public Promise<Void> show(FormInstance instance) {
        return resourceLocator.getFormClass(instance.getId()).then(new Function<FormClass,Void>() {

            @Nullable
            @Override
            public Void apply(@Nullable FormClass input) {
                container.add(new HTML("<h1>" + input.getLabel().getValue() + "</h1>"));
                formPanel = new FormPanel(input, resourceLocator);
                formPanel.setDesignEnabled(true);
                container.add(formPanel);
                return null;
            }
        });

    }

    @Override
    public Widget asWidget() {
        return container;
    }
}
