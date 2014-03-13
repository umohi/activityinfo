package org.activityinfo.ui.full.client.page.instance;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.ui.full.client.page.PageId;
import org.activityinfo.ui.full.client.page.PageState;
import org.activityinfo.ui.full.client.page.PageStateParser;
import org.activityinfo.ui.full.client.page.app.Section;

import java.util.List;

/**
 * Place corresponding to the view of a instance.
 */
public class InstancePlace implements PageState {

    private Cuid instanceId;

    public InstancePlace(Cuid instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String serializeAsHistoryToken() {
        return instanceId.asString();
    }

    @Override
    public PageId getPageId() {
        return InstancePage.PAGE_ID;
    }

    public Cuid getInstanceId() {
        return instanceId;
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Lists.newArrayList(InstancePage.PAGE_ID);
    }

    @Override
    public Section getSection() {
        return null;
    }

    public static class Parser implements PageStateParser {

        @Override
        public PageState parse(String token) {
            return new InstancePlace(new Cuid(token));
        }
    }


    public static SafeUri safeUri(Cuid instanceId) {
        return UriUtils.fromTrustedString("#i/" + instanceId.asString());
    }
}
