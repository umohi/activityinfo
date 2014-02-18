package org.activityinfo.api2.server.form;

import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.QuantityFormatter;
import org.activityinfo.api2.shared.form.QuantityFormatterFactory;

import java.text.NumberFormat;

/**
 * Created by alex on 2/19/14.
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
