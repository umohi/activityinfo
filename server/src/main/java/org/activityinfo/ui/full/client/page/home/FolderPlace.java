package org.activityinfo.ui.full.client.page.home;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.PageStateParser;
import org.activityinfo.ui.full.client.page.app.Section;

import java.util.List;

/**
 *
 */
public class FolderPlace implements PageState {

    private final Cuid folderId;

    public FolderPlace(Cuid folderId) {
        this.folderId = folderId;
    }

    public Cuid getFolderId() {
        return folderId;
    }

    @Override
    public String serializeAsHistoryToken() {
        return folderId.asString();
    }

    @Override
    public PageId getPageId() {
        return PageContainer.PAGE_ID;
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Lists.newArrayList(PageContainer.PAGE_ID);
    }

    @Override
    public Section getSection() {
        return null;
    }

    public static class Parser implements PageStateParser {

        @Override
        public PageState parse(String token) {
            if(token == null) {
                return new FolderPlace(null);
            }
            return new FolderPlace(new Cuid(token));
        }
    }
}
