package org.activityinfo.core.server.formatter;

import org.activityinfo.core.shared.formatter.QuantityFormatter;
import org.activityinfo.core.shared.formatter.QuantityFormatterFactory;

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
            public String format(Double value) {
                return format.format(value);
            }

            @Override
            public Double parse(String valueAsString) {
                try {
                    return format.parse(valueAsString).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace(); // todo log exception
                    return null;
                }
            }
        };
    }
}
