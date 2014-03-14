package org.activityinfo.core.shared.serialization;

import java.util.List;

/**
 * A serialized array
 */
public class SerArray extends SerValue {

    private List<SerValue> array;

    public int size() {
        return array.size();
    }

    public void add(SerValue value) {
        array.add(value);
    }

    public SerValue get(int index) {
        return array.get(index);
    }

    public void add(double value) {
        array.add(new SerReal(value));
    }

    public void add(String value) {
        array.add(new SerString(value));
    }
}
