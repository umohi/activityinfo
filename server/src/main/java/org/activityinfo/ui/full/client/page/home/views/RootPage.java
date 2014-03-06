package org.activityinfo.ui.full.client.page.home.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.system.FolderClass;
import org.activityinfo.ui.full.client.page.home.list.ItemRenderer;

import java.util.List;

/**
 * Page that shows a list of root folder accessible by the user
 */
public class RootPage implements IsWidget {

    private final HTMLPanel panel;

    interface RootPageUiBinder extends UiBinder<HTMLPanel, RootPage> {
    }

    private static RootPageUiBinder ourUiBinder = GWT.create(RootPageUiBinder.class);

    @UiField
    DivElement folderListElement;

    public RootPage(List<FormInstance> rootItems) {
        panel = ourUiBinder.createAndBindUi(this);

        ItemRenderer renderer = GWT.create(ItemRenderer.class);

        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for(FormInstance instance : rootItems) {
           renderer.render(html, instance.getString(FolderClass.LABEL_FIELD_ID),
                   instance.getString(FolderClass.DESCRIPTION_FIELD_ID),
                   "#folder/" + instance.getId().asString());
        }
        folderListElement.setInnerSafeHtml(html.toSafeHtml());
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}