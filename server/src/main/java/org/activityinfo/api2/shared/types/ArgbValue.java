package org.activityinfo.api2.shared.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ArgbValue extends FieldValue {

    private double alpha;
    private int red;
    private int green;
    private int blue;

    public double getAlpha() {
        return alpha;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    @Override
    public String getTypeClassId() {
        return ArgbColorType.TYPE_CLASS_ID;
    }

    @Override
    public JsonElement serialize() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(alpha));
        array.add(new JsonPrimitive(red));
        array.add(new JsonPrimitive(green));
        array.add(new JsonPrimitive(blue));
        return array;
    }
}
