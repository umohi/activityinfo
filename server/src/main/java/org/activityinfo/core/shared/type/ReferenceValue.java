package org.activityinfo.core.shared.type;

import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.serialization.SerArray;
import org.activityinfo.core.shared.serialization.SerValue;

import java.util.Set;

/**
 * A reference to another instance
 */
public class ReferenceValue implements FieldValue {

    public static final String TYPE_CLASS_ID = "reference";

    private Set<Cuid> instanceIds;

    private ReferenceValue() {}


    public ReferenceValue(Set<Cuid> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public ReferenceValue(Cuid... instanceIds) {
        this.instanceIds = Sets.newHashSet(instanceIds);
    }

    public Iterable<Cuid> getInstanceIds() {
        return instanceIds;
    }

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    @Override
    public SerValue serialize() {
        SerArray array = new SerArray();
        for(Cuid id : instanceIds) {
            array.add(id.asString());
        }
        return array;
    }

    public static class Parser implements FieldValueParser {

        @Override
        public ReferenceValue parse(SerValue value) {
            SerArray array = value.asArray();
            ReferenceValue fieldValue = new ReferenceValue();
            fieldValue.instanceIds = Sets.newHashSet();

            for(int i=0;i!=array.size();++i) {
                fieldValue.instanceIds.add(new Cuid(array.get(i).asString()));
            }
            return fieldValue;
        }
    }
}
