package org.activityinfo.api2.client.form;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gwt.json.client.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormElement;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.FormSection;
import org.activityinfo.api2.shared.model.AiLatLng;
import org.codehaus.jackson.annotate.JsonValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alex on 3/8/14.
 */
public class InstanceSerializer {



    public static JsonObject toJson(FormInstance instance) {
        JsonObject object = new JsonObject();
        object.addProperty("id", instance.getId().asString());
        object.addProperty("class", instance.getClassId().asString());
        object.addProperty("parent", instance.getParentId().asString());

        JsonObject values = new JsonObject();
        for(Map.Entry<Cuid, Object> entry : instance.getValueMap().entrySet()) {
            values.add(entry.getKey().asString(), valueToJson(entry.getValue()));
        }
        object.add("values", values);

        return object;
    }

    static JsonElement valueToJson(Object value) {
        if(value instanceof String) {
            return new JsonPrimitive((String)value);
        } else if(value instanceof Number) {
            return new JsonPrimitive(((Number)value).doubleValue());
        }

        // otherwise we need to wrap along with type descriptor
        JsonObject typedValue = new JsonObject();

        if(value instanceof AiLatLng) {
            AiLatLng point = (AiLatLng) value;
            typedValue.addProperty("type", "point");
            typedValue.addProperty("x", point.getLat());
            typedValue.addProperty("y", point.getLng());

        } else if(value instanceof Date) {
            LocalDate localDate = new LocalDate((Date) value);
            typedValue.addProperty("type", "localDate");
            typedValue.addProperty("value", localDate.toString());

        } else if(value instanceof Set) {
            Set<Cuid> ids = (Set)value;
            typedValue.addProperty("type", "reference");
            JsonArray array = new JsonArray();
            for(Cuid cuid : ids) {
                array.add(new JsonPrimitive(cuid.asString()));
            }
            typedValue.add("value", array);

        } else if(value instanceof Cuid) {
            Cuid id = (Cuid) value;
            typedValue.addProperty("type", "reference");
            typedValue.addProperty("value", id.asString());


        } else {
            throw new UnsupportedOperationException(value.getClass().getName());
        }
        return typedValue;
    }

}
