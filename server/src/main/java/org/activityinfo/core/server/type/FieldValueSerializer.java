package org.activityinfo.core.server.type;

import org.activityinfo.core.shared.serialization.SerArray;
import org.activityinfo.core.shared.serialization.SerObject;
import org.activityinfo.core.shared.serialization.SerValue;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;

/**
 * Serializes FieldValues via the Jackson API
 */
public class FieldValueSerializer {

    private JsonGenerator generator;

    public FieldValueSerializer(JsonGenerator generator) {
        this.generator = generator;
    }

    public void serialize(SerValue serValue) throws IOException {
        if(serValue.isString()) {
            generator.writeString(serValue.asString());

        } else if(serValue.isReal()) {
            generator.writeNumber(serValue.asReal());

        } else if(serValue.isArray()) {
            SerArray array = serValue.asArray();

            generator.writeStartArray();
            for(int i=0;i!=array.size();++i) {
                serialize(array.get(i));
            }
            generator.writeEndArray();

        } else if(serValue.isObject()) {
            SerObject object = serValue.asObject();

            generator.writeStartObject();
            for(String key : object.keySet()) {
                generator.writeFieldName(key);
                serialize(object.get(key));
            }
            generator.writeEndObject();
        }
    }
}
