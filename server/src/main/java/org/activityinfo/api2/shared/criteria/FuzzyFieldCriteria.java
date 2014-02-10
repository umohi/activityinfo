package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nullable;

/**
 * Matches a string
 */
public class FuzzyFieldCriteria implements Criteria {

    private final Cuid fieldId;
    private final String value;
    private final double threshold;

    public FuzzyFieldCriteria(Cuid fieldId, String value, double threshold) {
        this.fieldId = fieldId;
        this.value = value;
        this.threshold = threshold;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitFuzzyFieldCriteria(this);
    }

    @Override
    public boolean apply(@Nullable FormInstance input) {
        String inputValue = input.getString(fieldId);
        return JaroWinklerDistance.DEFAULT.getDistance(value, inputValue) > threshold;
    }
}
