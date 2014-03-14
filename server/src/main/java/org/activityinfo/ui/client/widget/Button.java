package org.activityinfo.ui.client.widget;

import com.google.gwt.uibinder.client.UiConstructor;

/**
 * Subclass of {@link org.activityinfo.ui.client.widget.ButtonWithSize} that allows to create button without size
 * definition (default).
 */
public class Button extends ButtonWithSize {

    @UiConstructor
    public Button(ButtonStyle style) {
        super(style, null);
    }
}
