package org.activityinfo.ui.full.client.widget;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.i18n.shared.I18N;

/**
 * SimpleLayoutPanel for widgets that need to be asynchronously
 * created
 */
public class AsyncPanel extends SimpleLayoutPanel {

    public void setWidget(Function<Void, Promise<IsWidget>> provider) {

        setWidget(new Label(I18N.CONSTANTS.loading()));
        provider.apply(null).then(new AsyncCallback<IsWidget>() {
            @Override
            public void onFailure(Throwable caught) {
                setWidget(new Label("Failed: " + caught));
            }

            @Override
            public void onSuccess(IsWidget result) {
                setWidget(result);
            }
        });

    }

}
