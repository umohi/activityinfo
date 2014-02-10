package org.activityinfo.api2.shared.criteria;


import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class CriteriaUnion implements Criteria, Iterable<Criteria> {

    private final List<Criteria> criteria;

    public CriteriaUnion(Iterable<? extends Criteria> criteria) {
        this.criteria = Lists.newArrayList(criteria);
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitUnion(this);
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        for(Criteria criterium : criteria) {
            if(criterium.apply(instance)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Criteria> iterator() {
        return criteria.iterator();
    }
}
