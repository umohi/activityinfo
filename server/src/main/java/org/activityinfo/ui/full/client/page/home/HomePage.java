package org.activityinfo.ui.full.client.page.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.application.FolderClass;
import org.activityinfo.ui.full.client.page.NavigationCallback;
import org.activityinfo.ui.full.client.page.Page;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.home.list.ListItemRenderer;

import java.util.List;

/**
 * Page that shows a list of root folder accessible by the user
 */
public class HomePage implements Page {

    public static final PageId PAGE_ID = new PageId("home");

    private final HTMLPanel panel;

    interface RootPageUiBinder extends UiBinder<HTMLPanel, HomePage> {
    }

    private static RootPageUiBinder ourUiBinder = GWT.create(RootPageUiBinder.class);

    @UiField
    DivElement folderListElement;

    public HomePage(List<FormInstance> rootItems) {
        panel = ourUiBinder.createAndBindUi(this);

        ListItemRenderer renderer = GWT.create(ListItemRenderer.class);

        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for(FormInstance instance : rootItems) {
           renderer.render(html, instance.getString(FolderClass.LABEL_FIELD_ID),
                   instance.getString(FolderClass.DESCRIPTION_FIELD_ID),
                   "#i/" + instance.getId().asString());
        }
        folderListElement.setInnerSafeHtml(html.toSafeHtml());
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return panel;
    }

    @Override
    public void requestToNavigateAway(PageState place, NavigationCallback callback) {
        callback.onDecided(true);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        return false;
    }

    @Override
    public void shutdown() {

    }

}