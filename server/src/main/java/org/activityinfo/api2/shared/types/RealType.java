package org.activityinfo.api2.shared.types;

import com.google.gson.JsonElement;
import org.activityinfo.api2.shared.serialization.DataValueParser;

/**
 * Real-valued quantity with units
 */
public class RealType extends DataType {

    public static final String TYPE_ID = "quantity";

    private String units;

    @Override
    public String getTypeClassId() {
        return TYPE_ID;
    }

    public String getUnits() {
        return units;
    }



    public static class Parser implements DataValueParser<RealValue> {

        @Override
        public RealValue deserialize(JsonElement element) {
            return new RealValue(element.getAsDouble());
        }
    }
}
