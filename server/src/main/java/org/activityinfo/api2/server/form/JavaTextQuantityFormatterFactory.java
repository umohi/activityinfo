package org.activityinfo.api2.server.form;

import org.activityinfo.api2.shared.form.QuantityFormatter;
import org.activityinfo.api2.shared.form.QuantityFormatterFactory;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Creates a formatter for a field using the standard Java API
 */
public class JavaTextQuantityFormatterFactory implements QuantityFormatterFactory {
    @Override
    public QuantityFormatter create() {
        final NumberFormat format = NumberFormat.getNumberInstance();
        return new QuantityFormatter() {
            @Override
            public String format(double value) {
                return format.format(value);
            }

            @Override
            public double parse(String valueAsString) {
                try {
                    return format.parse(valueAsString).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace(); // todo log exception
                    return -1;
                }
            }
        };
    }
}
