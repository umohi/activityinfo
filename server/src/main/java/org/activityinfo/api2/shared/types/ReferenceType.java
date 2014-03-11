package org.activityinfo.api2.shared.types;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.serialization.DataValueParser;

import java.util.Set;

/**
 * A property whose range is a set of related instances
 */
public class ReferenceType extends DataType {

    public static final String TYPE_CLASS_ID = "reference";

    private Criteria range;

    public void setRange(Criteria range) {
        this.range = range;
    }

    public Criteria getRange() {
        return range;
    }

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    @Override
    public JsonElement serialize() {
        return null;
    }

    public static class ParserData implements DataValueParser<ReferenceValue> {

        @Override
        public JsonElement serialize(ReferenceValue value) {

        }

        @Override
        public ReferenceValue deserialize(JsonElement element) {
            JsonArray array = element.getAsJsonArray();
            Set<Cuid> ids = Sets.newHashSet();
            for(int i=0;i!=array.size();++i) {
                ids.add(new Cuid(array.get(i).getAsString()));
            }
            return new ReferenceValue(ids);
        }
    }
}
