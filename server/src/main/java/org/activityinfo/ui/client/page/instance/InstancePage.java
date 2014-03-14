package org.activityinfo.ui.client.page.instance;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.IdCriteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.fp.client.PromiseMonitor;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.page.NavigationCallback;
import org.activityinfo.ui.client.page.Page;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.pageView.InstancePageView;
import org.activityinfo.ui.client.pageView.folder.FolderPageView;
import org.activityinfo.ui.client.pageView.formClass.FormClassPageView;

import java.util.List;

/**
 * Adapter that hosts a view of a given instance.
 */
public class InstancePage implements Page, PromiseMonitor {
    public static final PageId PAGE_ID = new PageId("i");

    private SimpleLayoutPanel panel;
    private final ResourceLocator resourceLocator;


    public InstancePage(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
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
        resourceLocator.queryInstances(new IdCriteria(instancePlace.getInstanceId()))
            .then(new AsyncCallback<List<FormInstance>>() {
                @Override
                public void onFailure(Throwable caught) {
                    Log.debug("Navigation failed", caught);
                }

                @Override
                public void onSuccess(List<FormInstance> formInstances) {
                    if (formInstances.size() == 0) {
                        panel.setWidget(new HTML("Not found"));
                    } else {
                        loadView(formInstances.get(0));
                    }
                }
            });
        return true;
    }

    @Override
    public void shutdown() {

    }

    private void loadView(FormInstance instance) {
        InstancePageView view = createView(instance);
        view.show(instance);
        panel.setWidget(view);
    }

    private InstancePageView createView(FormInstance instance) {
        Cuid classId = instance.getClassId();
        if(classId.equals(FolderClass.CLASS_ID)) {
            return new FolderPageView(resourceLocator);
        } else if(classId.equals(FormClass.CLASS_ID)) {
            return new FormClassPageView(resourceLocator);
        } else {
            throw new UnsupportedOperationException();
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
