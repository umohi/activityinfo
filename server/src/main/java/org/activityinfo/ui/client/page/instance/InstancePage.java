package org.activityinfo.ui.client.page.instance;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.Resources;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.ui.client.page.NavigationCallback;
import org.activityinfo.ui.client.page.Page;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.pageView.InstancePageViewFactory;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

/**
 * Adapter that hosts a view of a given instance.
 */
public class InstancePage implements Page {
    public static final PageId PAGE_ID = new PageId("i");


    // scrollpanel.bs > div.container > loadingPanel
    private final ScrollPanel scrollPanel;
    private final SimplePanel container;
    private final LoadingPanel<FormInstance> loadingPanel;

    private final Resources resources;

    public InstancePage(ResourceLocator resourceLocator) {
        this.resources = new Resources(resourceLocator);

        Icons.INSTANCE.ensureInjected();

        this.loadingPanel = new LoadingPanel<>(new PageLoadingPanel());

        this.container = new SimplePanel(loadingPanel.asWidget());
        this.container.addStyleName("container");

        this.scrollPanel = new ScrollPanel(container);
        this.scrollPanel.addStyleName("bs");

        // set scroll ancestor, child widgets may want to "watch" it to provide own scrolling (e.g. table)
        // and avoid double scrolls as well as "correct" height
        this.loadingPanel.setScrollAncestor(scrollPanel);

    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Object getWidget() {
        return scrollPanel;
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
        InstancePlace instancePlace = (InstancePlace) place;
        this.loadingPanel.setDisplayWidgetProvider(
                new InstancePageViewFactory(resources.resourceLocator,
                (InstancePlace)place));
        loadingPanel.show(resources.fetchInstance(), instancePlace.getInstanceId());
        return true;
    }

    @Override
    public void shutdown() {

    }
}
