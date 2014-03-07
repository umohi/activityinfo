package org.activityinfo.api2.client.form;

import com.google.gwt.i18n.client.NumberFormat;
import org.activityinfo.api2.shared.form.QuantityFormatter;
import org.activityinfo.api2.shared.form.QuantityFormatterFactory;

/**
 * Creates QuantityFormatters using the GWT i18n classes.
 */
public class GwtQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create() {
        final NumberFormat format = NumberFormat.getDecimalFormat();
        return new QuantityFormatter() {
            @Override
            public String format(double value) {
                return format.format(value);
            }

            @Override
            public double parse(String valueAsString) {
                return format.parse(valueAsString);
            }
        };
    }
}
