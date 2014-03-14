package org.activityinfo.core.client.type;

import com.google.gwt.json.client.*;
import org.activityinfo.core.shared.serialization.SerArray;
import org.activityinfo.core.shared.serialization.SerObject;
import org.activityinfo.core.shared.serialization.SerValue;
import org.activityinfo.core.shared.type.FieldValue;

/**
 * Serializes SerValues to JSON
 */
public class FieldValueSerializer {


    public static JSONObject toJSON(FieldValue value) {
        JSONObject root = new JSONObject();
        root.put("type", new JSONString(value.getTypeClassId()));
        root.put("value", toJSON(value.serialize()));
        return root;
    }

    private static JSONValue toJSON(SerValue serValue) {
        if(serValue.isString()) {
            return new JSONString(serValue.asString());

        } else if(serValue.isReal()) {
            return new JSONNumber(serValue.asReal());

        } else if(serValue.isArray()) {
            SerArray serArray = serValue.asArray();
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i!=serArray.size();++i) {
                jsonArray.set(i, toJSON(serArray.get(i)));
            }
            return jsonArray;

        } else if(serValue.isObject()) {
            SerObject serObject = serValue.asObject();
            JSONObject jsonObject = new JSONObject();
            for(String key : serObject.keySet()) {
                jsonObject.put(key, toJSON(serObject.get(key)));
            }
            return jsonObject;

        } else {
            throw new IllegalArgumentException();
        }
    }
}
