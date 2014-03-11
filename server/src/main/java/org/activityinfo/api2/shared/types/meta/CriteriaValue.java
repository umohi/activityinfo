package org.activityinfo.api2.shared.types.meta;

import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.types.FieldValue;

public class CriteriaValue extends FieldValue {

    private Criteria value;

    public CriteriaValue(Criteria criteria) {
        this.value = criteria;
    }

    public Criteria getValue() {
        return value;
    }

    @Override
    public String getTypeClassId() {
        return CriteriaPropertyType.TYPE_CLASS_ID;
    }
}
