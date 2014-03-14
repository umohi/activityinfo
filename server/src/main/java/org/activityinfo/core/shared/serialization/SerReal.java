package org.activityinfo.core.shared.serialization;

/**
 * A serialized 64-bit floating point number
 */
public class SerReal extends SerValue {

    private final double value;

    public SerReal(double value) {
        this.value = value;
    }

    @Override
    public boolean isReal() {
        return true;
    }

    @Override
    public double asReal() {
        return value;
    }
}
