package org.activityinfo.api2.shared.serialization;

import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.types.RealType;
import org.activityinfo.api2.shared.types.ReferenceType;
import org.activityinfo.api2.shared.types.TextDataType;

import java.util.Map;

/**
 * Provides a lookup map of value serializers/deserializers (see {@link DataValueParser}
 * by type class id.
 */
public class ValueSerdes {

    private static Map<String, DataValueParser<?>> TYPE_MAP;

    public static DataValueParser<?> forId(String typeClassId) {
        if(TYPE_MAP == null) {
            TYPE_MAP = Maps.newHashMap();
            TYPE_MAP.put(RealType.TYPE_ID, new RealType.Parser());
            TYPE_MAP.put(ReferenceType.TYPE_CLASS_ID, new ReferenceType.ParserData());
            TYPE_MAP.put(TextDataType.TYPE_CLASS_ID, new ReferenceType.ParserData());
        }
        return TYPE_MAP.get(typeClassId);
    }
}
