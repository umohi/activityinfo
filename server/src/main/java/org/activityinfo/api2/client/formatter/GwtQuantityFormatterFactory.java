package org.activityinfo.api2.client.formatter;

import com.google.gwt.i18n.client.NumberFormat;
import org.activityinfo.api2.shared.formatter.QuantityFormatter;
import org.activityinfo.api2.shared.formatter.QuantityFormatterFactory;

/**
 * Creates QuantityFormatters using the GWT i18n classes.
 */
public class GwtQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create() {
        final NumberFormat format = NumberFormat.getDecimalFormat();
        return new QuantityFormatter() {
            @Override
            public String format(Double value) {
                return format.format(value);
            }

            @Override
            public Double parse(String valueAsString) {
                return format.parse(valueAsString);
            }
        };
    }
}
