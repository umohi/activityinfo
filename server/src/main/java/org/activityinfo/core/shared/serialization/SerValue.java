package org.activityinfo.core.shared.serialization;

/**
 * A serialized value
 */
public abstract class SerValue {

    public boolean isString() {
        return false;
    }

    public String asString() {
        throw new SerTypeException();
    }

    public boolean isArray() {
        return false;
    }

    public SerArray asArray() {
        throw new SerTypeException();
    }

    public boolean isReal() {
        return false;
    }

    public double asReal() {
        throw new SerTypeException();
    }

    public int asInteger() {
        return (int)asReal();
    }

    public boolean isObject() {
        return false;
    }

    public SerObject asObject() {
        throw new SerTypeException();
    }

}
