package org.activityinfo.api2.shared.criteria;

/**
 * Created by alex on 2/10/14.
 */
public interface CriteriaVisitor {

    void visitFieldCriteria(FieldCriteria criteria);

    void visitClassCriteria(ClassCriteria criteria);
}
