package org.activityinfo.api2.shared.types;

/**
 * Wraps a quantity
 */
public class RealValue extends FieldValue {

    private double value;

    public RealValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getTypeClassId() {
        return RealType.TYPE_ID;
    }

    public static FieldValue valueOf(Number number) {
        return new RealValue(number.doubleValue());
    }
}
