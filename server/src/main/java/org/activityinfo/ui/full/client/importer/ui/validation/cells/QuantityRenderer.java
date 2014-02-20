package org.activityinfo.ui.full.client.importer.ui.validation.cells;

import com.google.common.base.Function;
import com.google.gwt.i18n.client.NumberFormat;
import org.activityinfo.api2.shared.form.QuantityFormatter;

import javax.annotation.Nullable;

/**
 * Renders a QUANTITY field value to SafeHtml
 */
public class QuantityRenderer implements Function<Object, String> {

    private QuantityFormatter formatter;

    public QuantityRenderer(QuantityFormatter formatter) {
        this.formatter = formatter;
    }

    @Nullable
    @Override
    public String apply(@Nullable Object input) {
        return input == null ? null : formatter.format((Double)input);
    }
}
