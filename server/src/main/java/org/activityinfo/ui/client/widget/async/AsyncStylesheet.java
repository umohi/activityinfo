package org.activityinfo.ui.client.widget.async;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.client.Stylesheet;
import com.google.gwt.core.client.GWT;


@Source("Async.less")
public interface AsyncStylesheet extends Stylesheet {

    public static final AsyncStylesheet INSTANCE = GWT.create(AsyncStylesheet.class);

    String asyncWidget();

    String innerBox();

    String outerBox();
}
