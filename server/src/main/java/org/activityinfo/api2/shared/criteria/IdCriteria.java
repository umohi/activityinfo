package org.activityinfo.api2.shared.criteria;

import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Matches Instances that belong to the given set
 */
public class IdCriteria implements Criteria {

    private final Set<Cuid> instanceIds;

    public IdCriteria(Cuid... instanceIds) {
        this.instanceIds = Sets.newHashSet(instanceIds);
    }

    public IdCriteria(Set<Cuid> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public Set<Cuid> getInstanceIds() {
        return instanceIds;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitInstanceIdCriteria(this);
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return instanceIds.contains(instance.getId());
    }
}
