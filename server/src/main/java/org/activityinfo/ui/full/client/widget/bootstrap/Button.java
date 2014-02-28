package org.activityinfo.ui.full.client.widget.bootstrap;

import com.google.gwt.uibinder.client.UiConstructor;

/**
 * Shallow subclass of Button that applies Bootstrap Styles
 */
public class Button extends com.google.gwt.user.client.ui.Button {

    public enum ButtonStyle {
        DEFAULT,

        /**
         * Provides extra visual weight and identifies the primary action in a set of buttons
         */
        PRIMARY,

        /**
         * Indicates a successful or positive action
         */
        SUCCESS,

        /**
         * Contextual button for informational alert messages
         */
        INFO,

        /**
         * Indicates caution should be taken with this action
         */
        WARNING,

        /**
         *  Indicates a dangerous or potentially negative action
         */
        DANGER,

        /**
         *  Deemphasize a button by making it look like a link while maintaining button behavior
         */
        LINK
    }

    @UiConstructor
    public Button(ButtonStyle style) {
        setStyleName("btn btn-" + style.name().toLowerCase());
    }

}
