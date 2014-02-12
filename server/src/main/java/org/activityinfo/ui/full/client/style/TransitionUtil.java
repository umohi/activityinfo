package org.activityinfo.ui.full.client.style;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.StyleInjector;
import org.activityinfo.ui.full.client.util.FeatureSwitch;

public class TransitionUtil {

    private static boolean injected = false;

    private TransitionUtil() {
    }

    public static void ensureBootstrapInjected() {
        if (!injected) {
            TransitionBundle bundle = GWT.create(TransitionBundle.class);
            StyleInjector.inject(bundle.bootstrapStyle().getText());
            if (FeatureSwitch.isNewFormEnabled()) {
                StyleInjector.inject(bundle.gwtStyle().getText());
            }
            injected = true;
        }
    }
}
