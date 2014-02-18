package org.activityinfo.api2.shared.form;

/**
 * Formats the value of a QUANTITY FormField as a String
 */
public interface QuantityFormatter {

    String format(double value);
}
