package org.activityinfo.core.client;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.criteria.NullCriteria;
import org.activityinfo.core.shared.form.tree.FieldPath;

import java.util.List;

/**
 * Describes a query for {@code FormInstances}
 */
public class InstanceQuery {

    private final List<FieldPath> fieldPaths;
    private final Criteria criteria;

    public InstanceQuery(List<FieldPath> fieldPaths, Criteria criteria) {
        assert criteria != null;
        this.criteria = criteria;
        this.fieldPaths = fieldPaths;
    }

    public List<FieldPath> getFieldPaths() {
        return fieldPaths;
    }

    public Criteria getCriteria() {
        return criteria;
    }


    public static Builder select(Cuid... fieldIds) {
        return new Builder().select(fieldIds);
    }

    public static class Builder {
        private List<FieldPath> paths = Lists.newArrayList();
        private Criteria criteria;

        private Builder() {}

        public Builder where(Criteria criteria) {
            assert this.criteria == null : "Criteria already specified";
            this.criteria = criteria;
            return this;
        }

        public Builder select(Cuid... fields) {
            for(Cuid fieldId : fields) {
                paths.add(new FieldPath(fieldId));
            }
            return this;
        }

        public InstanceQuery build() {
            if(criteria == null) {
                criteria = NullCriteria.INSTANCE;
            }
            return new InstanceQuery(paths, criteria);
        }
    }
}
