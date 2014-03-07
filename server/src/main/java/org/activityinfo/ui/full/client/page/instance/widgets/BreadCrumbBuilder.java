package org.activityinfo.ui.full.client.page.instance.widgets;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.application.ApplicationProperties;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.page.instance.InstancePlace;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Displays a set of parent links
 */
public class BreadCrumbBuilder {

    public interface LinkTemplates extends SafeHtmlTemplates {

        @Template("<a href=\"{0}\">{1}</a>")
        SafeHtml link(SafeUri link, String label);
    }

    public static final LinkTemplates TEMPLATES = GWT.create(LinkTemplates.class);

    private final ResourceLocator resourceLocator;
    private final Element element;

    public BreadCrumbBuilder(ResourceLocator resourceLocator, Element element) {
        this.resourceLocator = resourceLocator;
        this.element = element;
    }

    public void show(FormInstance instance) {
        if(instance.getParentId() == null) {
            element.setInnerSafeHtml(TEMPLATES.link(UriUtils.fromTrustedString("#home"), I18N.CONSTANTS.home()));

        } else {
            element.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString("&nbsp;"));

            queryParent(instance.getParentId())
            .then(new Function<List<Projection>, Void>() {
                @Nullable
                @Override
                public Void apply(List<Projection> projections) {
                    Projection parent = projections.get(0);
                    element.setInnerSafeHtml(TEMPLATES.link(InstancePlace.safeUri(parent.getRootInstanceId()),
                            parent.getStringValue(ApplicationProperties.LABEL_PROPERTY)));

                    return null;
                }
            });
        }

    }

    private Promise<List<Projection>> queryParent(Cuid parentId) {
        return resourceLocator.query(InstanceQuery
                .select(ApplicationProperties.LABEL_PROPERTY)
                .where(new IdCriteria(parentId))
                .build());
    }

}
