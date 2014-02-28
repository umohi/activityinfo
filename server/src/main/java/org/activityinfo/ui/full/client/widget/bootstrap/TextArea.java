package org.activityinfo.ui.full.client.widget.bootstrap;

/**
 * TextArea subclass with Bootstrap style applied
 */
public class TextArea extends com.google.gwt.user.client.ui.TextArea {

    public TextArea() {
        super();
        setStyleName("form-control");
    }

    public void setPlaceholder(String text) {
        getElement().setAttribute("placeholder", text);
    }

}
