package org.activityinfo.ui.full.client.importer.process;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.client.promises.Action;
import org.activityinfo.api2.client.promises.AsyncFunctions;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MatchField;
import org.activityinfo.ui.full.client.importer.binding.MatchTable;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by alex on 2/19/14.
 */
public class AsyncReferenceMatcher implements AsyncFunction<MappedReferenceFieldBinding, Void> {

    private final Scheduler scheduler;
    private final ResourceLocator resourceLocator;
    private final SourceTable sourceTable;

    public AsyncReferenceMatcher(Scheduler scheduler, ResourceLocator resourceLocator, SourceTable sourceTable) {
        this.scheduler = scheduler;
        this.resourceLocator = resourceLocator;
        this.sourceTable = sourceTable;
    }

    @Override
    public void apply(final MappedReferenceFieldBinding binding, AsyncCallback<Void> callback) {

        final List<FieldPath> matchFields = Lists.newArrayList();
        for(MatchField matchField : binding.getMatchFields()) {
            matchFields.add(matchField.getRelativeFieldPath());
        }

        resourceLocator.query(matchFields, binding.getRange())
            .then(new AsyncFunction<List<Projection>, Void>() {
                @Override
                public void apply(@Nullable List<Projection> projections, AsyncCallback<Void> callback) {

                    MatchBinder matchFunction =
                            new MatchBinder(
                            new ReferenceMatchFunction(binding.getMatchFields(), projections),
                                    binding.getMatchTable());

                    AsyncFunctions.mapNonBlocking(scheduler, matchFunction).apply(sourceTable.getRows(), callback);
                }
            })
            .then(callback);
    }

    private class MatchBinder extends Action<SourceRow> {
        private final Function<SourceRow, ScoredReference>  matchFunction;
        private final MatchTable matchTable;

        private MatchBinder(Function<SourceRow, ScoredReference> matchFunction, MatchTable matchTable) {
            this.matchFunction = matchFunction;
            this.matchTable = matchTable;
        }

        @Override
        public void execute(SourceRow input) {
            matchTable.setMatch(input.getRowIndex(), matchFunction.apply(input));
        }
    }

}
