package org.activityinfo.ui.full.client.importer.ui.validation.cells;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Function;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;

import javax.annotation.Nullable;

/**
 * Renders a LocalDate to SafeHtml
 */
public class LocalDateRenderer implements Function<Object, String> {

    @Nullable
    @Override
    public String apply(@Nullable Object input) {
        return input == null ? null : input.toString();
    }
}
