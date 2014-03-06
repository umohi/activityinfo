package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;

/**
 * Criteria that filters on the parent's id
 */
public class ParentCriteria implements Criteria {

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitParentCriteria(this);
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return instance.getParentId() == null;
    }
}
