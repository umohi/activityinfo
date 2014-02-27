package org.activityinfo.api2.server.form;

import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.QuantityFormatter;
import org.activityinfo.api2.shared.form.QuantityFormatterFactory;

import java.text.NumberFormat;

/**
 * Creates a formatter for a field using the standard Java API
 */
public class JavaTextQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create(FormField field) {
        final NumberFormat format = NumberFormat.getNumberInstance();
        return new QuantityFormatter() {
            @Override
            public String format(double value) {
                return format.format(value);
            }
        };
    }
}
