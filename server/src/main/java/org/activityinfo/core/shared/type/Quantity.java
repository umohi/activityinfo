package org.activityinfo.core.shared.type;

import org.activityinfo.core.shared.serialization.SerReal;
import org.activityinfo.core.shared.serialization.SerValue;

/**
 * Real-valued quantity
 */
public class Quantity implements FieldValue {

    public static final String TYPE_CLASS_ID = "quantity";

    private double value;

    public Quantity(double value) {
        this.value = value;
    }

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    @Override
    public SerValue serialize() {
        return new SerReal(value);
    }

    public static class Parser implements FieldValueParser {
        @Override
        public FieldValue parse(SerValue value) {
            return new Quantity(value.asReal());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quantity quantity = (Quantity) o;

        if (Double.compare(quantity.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }
}
