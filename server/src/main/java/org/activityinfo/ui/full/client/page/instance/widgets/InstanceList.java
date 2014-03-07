package org.activityinfo.ui.full.client.page.instance.widgets;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.PromiseMonitor;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.page.instance.InstancePlace;

import javax.annotation.Nullable;
import java.util.List;

import static org.activityinfo.api2.shared.application.ApplicationProperties.*;

/**
 * Displays a list of instances
 */
public class InstanceList extends HTML implements PromiseMonitor {

    private final ListItemRenderer renderer;

    public InstanceList() {
        this.renderer = GWT.create(ListItemRenderer.class);
    }

    public void show(Promise<List<Projection>> instances) {
        instances.then(new Function<List<Projection>, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable List<Projection> projections) {
                SafeHtmlBuilder html = new SafeHtmlBuilder();
                for(Projection projection : projections) {
                    String label = projection.getStringValue(LABEL_PROPERTY);
                    String description = Strings.nullToEmpty(projection.getStringValue(DESCRIPTION_PROPERTY));
                    renderer.render(html, label, description,
                            InstancePlace.safeUri(projection.getRootInstanceId()).asString());
                }
                setHTML(html.toSafeHtml());
                return null;
            }
        })
        .withMonitor(this);
    }

    @Override
    public void onPromiseStateChanged(Promise.State state) {
        switch(state) {
            case PENDING:
                setText(I18N.CONSTANTS.loading());
                break;
            case REJECTED:
                setText(I18N.CONSTANTS.error());
                break;
        }
    }
}