package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.Cuid;

/**
 * Created by alex on 2/10/14.
 */
public class FieldCriteria {

    private Cuid fieldId;
    private Object value;
    private boolean allowFuzzy;

    public FieldCriteria(Cuid fieldId, Object value, boolean allowFuzzy) {
        this.fieldId = fieldId;
        this.value = value;
        this.allowFuzzy = allowFuzzy;
    }
}
