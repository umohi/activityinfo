package org.activityinfo.ui.full.client.component.list;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiRenderer;

/**
 * Renders an instance with an icon and a label
 */
public interface ListItemRenderer extends UiRenderer {

    void render(SafeHtmlBuilder sb, String label, String description, String link);

}
