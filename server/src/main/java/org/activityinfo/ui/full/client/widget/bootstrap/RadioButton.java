package org.activityinfo.ui.full.client.widget.bootstrap;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiConstructor;

/**
 * Shallow subclass of {@link RadioButton} that applies the Bootstrap style
 */
public class RadioButton extends com.google.gwt.user.client.ui.RadioButton {

    @UiConstructor
    public RadioButton(String name) {
        super(name);
        setStyleName("radio");
    }

    public RadioButton(String name, String label) {
        super(name, label);
        setStyleName("radio");
    }
}
