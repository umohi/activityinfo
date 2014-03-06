package org.activityinfo.api2.shared.criteria;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Criteria that filters on the parent's id
 */
public class ParentCriteria implements Criteria {

    private final Cuid parentId;

    private ParentCriteria(Cuid id) {
        this.parentId = id;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitParentCriteria(this);
    }

    public boolean selectsRoot() {
        return parentId == null;
    }

    public Cuid getParentId() {
        return parentId;
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return Objects.equals(parentId, instance.getParentId());
    }

    public static ParentCriteria isRoot() {
        return new ParentCriteria(null);
    }

    public static ParentCriteria isChildOf(Cuid id) {
        return new ParentCriteria(id);
    }
}
