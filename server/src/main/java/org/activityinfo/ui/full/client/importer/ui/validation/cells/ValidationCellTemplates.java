package org.activityinfo.ui.full.client.importer.ui.validation.cells;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
* Created by alex on 2/19/14.
*/
public interface ValidationCellTemplates extends SafeHtmlTemplates {

    @Template("<del>{0}</del>")
    public SafeHtml deleted(String text);

    @Template("<ins>{0}</ins>")
    public SafeHtml inserted(String text);

    @SafeHtmlTemplates.Template("<s>{0}</s>")
    public SafeHtml invalid(String text);
}
