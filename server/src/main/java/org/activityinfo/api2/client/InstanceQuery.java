package org.activityinfo.api2.client;

import org.activityinfo.api2.shared.criteria.Criteria;
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
}
