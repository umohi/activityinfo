package org.activityinfo.api2.client;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import java.util.List;

/**
 * Describes a query for {@code FormInstances}
 */
public class InstanceQuery {

    private final List<FieldPath> fieldPaths;
    private final Criteria criteria;

    public InstanceQuery(List<FieldPath> fieldPaths, Criteria criteria) {
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
            if(this.criteria != null) {
                throw new IllegalStateException("Criteria already specified");
            }
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
            return new InstanceQuery(paths, criteria);
        }
    }
}
