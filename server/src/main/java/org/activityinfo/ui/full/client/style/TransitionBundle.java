package org.activityinfo.ui.full.client.style;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

/**
 * Set of resources to support the transition from GXT to
 * a Bootstrapped-based site
 */
public interface TransitionBundle extends ClientBundle {

    public interface NewUiStyle extends CssResource {

        @ClassName("gwt-PopupPanelGlass")
        String gwtPopupPanelGlass();

        String ai();

        @ClassName("gwt-PopupPanel")
        String gwtPopupPanel();

        @ClassName("section-margin-left")
        String sectionMarginLeft();

        @ClassName("show-inline")
        String showInline();

        @ClassName("gwt-TextBox")
        String gwtTextBox();

        @ClassName("gwt-SuggestBox")
        String gwtSuggestBox();

        @ClassName("gwt-DateBox")
        String gwtDateBox();

        @ClassName("gwt-TextArea")
        String gwtTextArea();

        @ClassName("error-cell")
        String errorCell();
    }

    /**
     * The Bootstrap styles have been enclosed in this style
     * to avoid messing with rest of the GXT styles for the time being.
     */
    public static final String CONTAINER_STYLE = "bs";

    @Source("bootstrap-transition-min.css")
    TextResource bootstrapStyle();

    @Source("gwt-standard-transition.css")
    TextResource gwtStyle();

    @Source("newui-transition.css")
    NewUiStyle newUiStyle();
}
