package org.activityinfo.ui.full.client.page.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.activityinfo.ui.full.client.page.*;

public class PageContainerLoader implements PageLoader {
    private final Provider<PageContainer> folderPage;

    @Inject
    public PageContainerLoader(
            NavigationHandler pageManager,
            PageStateSerializer placeSerializer,
            Provider<PageContainer> folderPage) {

            this.folderPage = folderPage;

            pageManager.registerPageLoader(PageContainer.PAGE_ID, this);
            placeSerializer.registerParser(PageContainer.PAGE_ID, new FolderPlace.Parser());
        }

        @Override
        public void load(final PageId pageId, final PageState pageState,
        final AsyncCallback<Page> callback) {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onSuccess() {
                    PageContainer page = folderPage.get();
                    page.navigate(pageState);
                    callback.onSuccess(page);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        }

}
