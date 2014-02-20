package org.activityinfo.ui.full.client.importer.process;

import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.server.database.hibernate.entity.Project;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MatchFieldBinding;

import java.util.List;

/**
 * Provides a context for ReferenceMatchingOperations operations
 */
public class MatchContext {
    private MappedReferenceFieldBinding binding;
    private List<Projection> projections;

    public MatchContext(MappedReferenceFieldBinding binding) {
        this.binding = binding;
    }

    public MappedReferenceFieldBinding getBinding() {
        return binding;
    }

    public List<Projection> getProjections() {
        return projections;
    }

}
