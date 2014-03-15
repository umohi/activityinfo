package org.activityinfo.ui.client.widget.async;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Indicates that things are loading...
 */
public class LoadingWidget implements IsWidget {

    private HTML widget;

    public interface Templates extends SafeHtmlTemplates {
        @Template("<img class='center-block' src='{0}'>")
        public SafeHtml loading(SafeUri image);
    }

    public static final Templates TEMPLATES = GWT.create(Templates.class);

    public LoadingWidget() {
        widget = new HTML(TEMPLATES.loading(AsyncClientBundle.INSTANCE.loadingIcon().getSafeUri()));
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
