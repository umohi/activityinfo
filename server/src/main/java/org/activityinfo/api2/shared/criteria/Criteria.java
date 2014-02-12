package org.activityinfo.api2.shared.criteria;

import com.google.common.base.Predicate;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import javax.annotation.Nonnull;

/**
 * Superclass of {@code Criteria} that are used to select
 * {@code FormInstance}s
 */
public interface Criteria {

    void accept(CriteriaVisitor visitor);

    boolean apply(@Nonnull FormInstance instance);

}
