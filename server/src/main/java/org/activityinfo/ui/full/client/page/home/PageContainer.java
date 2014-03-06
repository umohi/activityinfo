package org.activityinfo.ui.full.client.page.home;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.common.base.Function;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.PromiseMonitor;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.CriteriaIntersection;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.criteria.ParentCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.system.FolderClass;
import org.activityinfo.ui.full.client.page.NavigationCallback;
import org.activityinfo.ui.full.client.page.Page;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.home.views.RootPage;
import org.activityinfo.ui.full.client.style.TransitionUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Displays a folder.
 */
public class PageContainer extends SimpleLayoutPanel implements Page, PromiseMonitor {

    public static final PageId PAGE_ID = new PageId("folder");

    private final ResourceLocator resourceLocator;

    @Inject
    public PageContainer(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;

        TransitionUtil.ensureBootstrapInjected();
    }

    @Override
    public PageId getPageId() {
        return PAGE_ID;
    }

    @Override
    public Widget getWidget() {
        return this;
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
        if(place instanceof FolderPlace) {
            return navigateToFolderPlace((FolderPlace) place);
        }
        return false;
    }

    private boolean navigateToFolderPlace(FolderPlace place) {
        if(place.getFolderId() == null) {
            return navigateToRoot();
        } else {
            CriteriaIntersection criteria = new CriteriaIntersection(
                    new IdCriteria(place.getFolderId()));

            resourceLocator.queryInstances(criteria)
                    .then(new Function<List<FormInstance>, Object>() {
                        @Nullable
                        @Override
                        public Object apply(List<FormInstance> formInstances) {
                            if (formInstances.size() != 1) {
                                throw new IllegalArgumentException();
                            }
                            navigate(formInstances.get(0));
                            return null;
                        }
                    })
                    .withMonitor(this);


        }
        return false;
    }

    private void navigate(FormInstance formInstance) {
        if(formInstance.getClassId().equals(FolderClass.FORM_CLASS)) {

        }
    }

    boolean navigateToRoot() {
        CriteriaIntersection criteria = new CriteriaIntersection(
                new ClassCriteria(FolderClass.FORM_CLASS),
                new ParentCriteria());
        resourceLocator.queryInstances(criteria)
                .then(new Function<List<FormInstance>, Object>() {
                    @Nullable
                    @Override
                    public Object apply(List<FormInstance> formInstances) {
                        setWidget(new RootPage(formInstances));
                        return null;
                    }
                })
                .withMonitor(this);


        return false;
    }

    public void shutdown() {

    }

    @Override
    public void onPromiseStateChanged(Promise.State state) {
        switch(state) {
            case PENDING:
                setWidget(new Html("Loading..."));
                break;
            case REJECTED:
                setWidget(new Html("Loading error. :-("));
                break;
        }
    }
}
