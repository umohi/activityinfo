package org.activityinfo.core.shared.importing.process;

import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.importing.binding.MappedReferenceFieldBinding;

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
