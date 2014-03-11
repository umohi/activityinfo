package org.activityinfo.api2.shared.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.activityinfo.api2.shared.serialization.DataValueParser;

/**
 * The Name Type Class is intended to cover a wide range of textual
 * identifiers used online and offline. A name is an unformatted character string that
 * describes a human readable name or a code used between systems. Examples of names include:
 *
 * <ul>
 *     <li>Place names</li>
 *     <li>People's names</li>
 *     <li>Telephone numbers</li>
 *     <li>URLs</li>
 *     <li>Fedex tracking codes</li>
 * </ul>
 */
public class TextDataType extends DataType<TextValue> {

    public static final String TYPE_CLASS_ID = "text";

    private boolean multiline;


    @Override
    public String getTypeClassId() {
        return TextDataType.TYPE_CLASS_ID;
    }

    public static class ParserData implements DataValueParser<TextValue> {

        @Override
        public JsonElement serialize(TextValue value) {
            return new JsonPrimitive(value.getValue());
        }

        @Override
        public TextValue deserialize(JsonElement element) {
            return new TextValue(element.getAsString());
        }
    }
}
