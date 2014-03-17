package org.activityinfo.ui.client.widget.async;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AsyncClientBundle extends ClientBundle {

    public static final AsyncClientBundle INSTANCE = GWT.create(AsyncClientBundle.class);

    @Source("large-loading.gif")
    ImageResource loadingIcon();
}
