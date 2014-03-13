package org.activityinfo.ui.full.client.widget;

/**
 * Subclass of {@code CheckBox} that applies our application styles
 */
public class CheckBox extends com.google.gwt.user.client.ui.CheckBox {

    public CheckBox() {
        setStyleName("checkbox");
    }

    public CheckBox(String label) {
        this();
        setText(label);
    }
}
