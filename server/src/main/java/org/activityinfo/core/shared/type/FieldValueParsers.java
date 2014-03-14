package org.activityinfo.core.shared.type;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by alex on 3/14/14.
 */
public class FieldValueParsers {

    private static FieldValueParsers INSTANCE = null;

    private Map<String, FieldValueParser> parsers = Maps.newHashMap();

    private FieldValueParsers() {
        parsers.put(FreeText.TYPE_CLASS_ID, new FreeText.Parser());
        parsers.put(Narrative.TYPE_CLASS_ID, new Narrative.Parser());
        parsers.put(ReferenceValue.TYPE_CLASS_ID, new Narrative.Parser());
        parsers.put(LocalDateValue.TYPE_CLASS_ID, new LocalDateValue.Parser());
        parsers.put(Quantity.TYPE_CLASS_ID, new Quantity.Parser());
    }

    public FieldValueParser get(String typeClassId) {
        if(INSTANCE == null) {
            INSTANCE = new FieldValueParsers();
        }
        FieldValueParser parser = parsers.get(typeClassId);
        if(parser == null) {
            throw new IllegalArgumentException(typeClassId);
        }
        return parser;
    }

}
