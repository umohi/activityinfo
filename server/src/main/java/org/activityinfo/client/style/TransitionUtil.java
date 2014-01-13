package org.activityinfo.client.style;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.StyleInjector;

public class TransitionUtil {

	private static boolean injected = false;
	
	public static void ensureBootstrapInjected() {
		if(!injected) {
			TransitionBundle bundle = GWT.create(TransitionBundle.class);
			StyleInjector.inject(bundle.bootstrapStyle().getText());
		}
	}
}
