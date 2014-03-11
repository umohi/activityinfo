package org.activityinfo.api2.shared.types;

import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Cuid;

import java.util.Set;

/**
 *
 */
public class ReferenceValue extends FieldValue {

    private Set<Cuid> referenceIds;

    public ReferenceValue(Set<Cuid> referenceIds) {
        this.referenceIds = referenceIds;
    }

    public ReferenceValue(Cuid... instanceIds) {
        this.referenceIds = Sets.newHashSet(instanceIds);
    }

    public Set<Cuid> getReferenceIds() {
        return referenceIds;
    }

    @Override
    public String getTypeClassId() {
        return ReferenceType.TYPE_CLASS_ID;
    }
}
