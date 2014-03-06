package org.activityinfo.ui.full.client.page.home.list;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * Created by alex on 3/4/14.
 */
public interface ListItemRenderer extends UiRenderer {

    void render(SafeHtmlBuilder sb, String label, String description, String link);

}
