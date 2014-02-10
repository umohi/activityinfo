package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;

/**
 * Null Criteria. (Includes all instances)
 */
public class NullCriteria implements Criteria {

    public static final NullCriteria INSTANCE = new NullCriteria();

    private NullCriteria() {

    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return true;
    }
}
