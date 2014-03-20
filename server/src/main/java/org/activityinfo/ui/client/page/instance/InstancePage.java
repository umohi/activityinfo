package org.activityinfo.ui.client.page.instance;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.Resources;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.IdCriteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.page.NavigationCallback;
import org.activityinfo.ui.client.page.Page;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.pageView.InstancePageViewFactory;
import org.activityinfo.ui.client.pageView.folder.FolderPageView;
import org.activityinfo.ui.client.pageView.formClass.FormClassPageView;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.DisplayWidget;
import org.activityinfo.ui.client.widget.LoadingPanel;
import org.activityinfo.ui.client.widget.loading.PageLoadingPanel;

import java.util.List;

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
        this.loadingPanel.setDisplayWidgetProvider(new InstancePageViewFactory(resourceLocator));

        this.container = new SimplePanel(loadingPanel.asWidget());
        this.container.addStyleName("container");

        this.scrollPanel = new ScrollPanel(container);
        this.scrollPanel.addStyleName("bs");


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
        loadingPanel.show(resources.fetchInstance(), instancePlace.getInstanceId());
        return true;
    }

    @Override
    public void shutdown() {

    }
}
