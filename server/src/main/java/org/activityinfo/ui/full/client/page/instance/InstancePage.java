package org.activityinfo.ui.full.client.page.instance;

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.PromiseMonitor;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.application.FolderClass;
import org.activityinfo.ui.full.client.page.NavigationCallback;
import org.activityinfo.ui.full.client.page.Page;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.instance.views.FolderView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Container that hosts a view of a given instance.
 */
public class InstancePage implements Page, PromiseMonitor {
    public static final PageId PAGE_ID = new PageId("i");

    public SimpleLayoutPanel panel;
    private ResourceLocator locator;

    public InstancePage(ResourceLocator locator) {
        this.locator = locator;
        this.panel = new SimpleLayoutPanel();
        this.panel.addStyleName("bs");
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
        InstancePlace instancePlace = (InstancePlace) place;
        locator.queryInstances(new IdCriteria(instancePlace.getInstanceId()))
            .then(new Function<List<FormInstance>, Void>() {
                @Nullable
                @Override
                public Void apply(List<FormInstance> formInstances) {
                    if(formInstances.size() == 0) {
                        panel.setWidget(new HTML("Not found"));
                    } else {
                        loadView(formInstances.get(0));
                    }
                    return null;
                }
            });
        return true;
    }

    @Override
    public void shutdown() {

    }

    private void loadView(FormInstance instance) {
        if(instance.getClassId().equals(FolderClass.CLASS_ID)) {
            FolderView folderView = new FolderView(locator);
            folderView.show(instance);
            panel.setWidget(folderView);
        }
    }

    @Override
    public void onPromiseStateChanged(Promise.State state) {
        switch(state) {
            case PENDING:
                panel.setWidget(new HTML("Loading..."));
                break;
            case REJECTED:
                panel.setWidget(new HTML("Error"));
                break;
        }
    }
}
