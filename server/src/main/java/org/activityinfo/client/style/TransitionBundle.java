package org.activityinfo.client.style;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Set of resources to support the transition from GXT to
 * a Bootstrapped-based site
 */
public interface TransitionBundle extends ClientBundle {

	/**
	 * The Bootstrap styles have been enclosed in this style
	 * to avoid messing with rest of the GXT styles for the time being.
	 */
	public static final String CONTAINER_STYLE = "bs";
	
	@Source("bootstrap-transition-min.css")
	TextResource bootstrapStyle();
	
}
