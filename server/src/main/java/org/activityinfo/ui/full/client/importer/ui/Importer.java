package org.activityinfo.ui.full.client.importer.ui;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.client.*;
import org.activityinfo.api2.client.promises.AsyncFunctions;
import org.activityinfo.api2.client.promises.IncrementalMapReduceFunction;
import org.activityinfo.api2.client.promises.Reducers;
import org.activityinfo.api2.client.promises.Result;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Pair;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.ui.full.client.importer.ImportModel;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MatchTable;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;
import org.activityinfo.ui.full.client.importer.process.*;
import org.activityinfo.ui.full.client.local.command.handler.KeyGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Functions.forPredicate;
import static com.google.common.collect.Iterables.filter;
import static org.activityinfo.api2.client.Promise.apply;
import static org.activityinfo.api2.client.promises.AsyncFunctions.*;
import static org.activityinfo.api2.client.promises.Reducers.count;
import static org.activityinfo.api2.client.promises.Reducers.countTrue;
import static org.activityinfo.api2.client.promises.Reducers.nullReducer;

/**
 * Client-side importer that is able to work offline
 */
public class Importer {

    private final ResourceLocator resourceLocator;
    private Scheduler scheduler;
    private Resources resources;
    private ImportModel importModel;
    private KeyGenerator keyGenerator;

    private Map<Integer, Cuid> rowIds = Maps.newHashMap();

    public Importer(Scheduler scheduler,
                    ResourceLocator resourceLocator,
                    ImportModel importModel) {
        this.scheduler = scheduler;

        this.resourceLocator = resourceLocator;
        this.resources = new Resources(resourceLocator);
        this.importModel = importModel;
    }

    public Promise<List<SourceRow>> getRows() {
        return Promise.resolved(importModel.getSource().getRows());
    }

    public Promise<Integer> countValidRows() {
        return getRows().then(mapReduce(scheduler, forPredicate(isValid()), countTrue()));
    }

    /**
     * Asynchronously matches imported fields against potential FormInstances to find the best
     * matches.
     */
    public Promise<Void> matchReferences() {

        Promise<Void> promise = Promise.resolved(null);

        List<MappedReferenceFieldBinding> fields = Lists.newArrayList();
        for(FieldBinding binding : createFieldBindings()) {
            if(binding instanceof MappedReferenceFieldBinding) {

                matchReferences(((MappedReferenceFieldBinding) binding));

                Promise<List<Projection>> projections = resources.query(.queryPotentialMatches())

            }
        }






    }

    private void matchReferences(MappedReferenceFieldBinding binding) {
        

        // get the list of possible matches for this REFERENCE field
        Promise<List<Projection>> projections = resources
                .query(binding.queryPotentialMatches());


        // our MatchRowFunction takes (projections, sourceRow) -> match,
        // so we use the curry function to get a new a function which takes
        // projections and gives us a function (projections -> (sourceRow -> match)
        // which we can chain with the query results
        Promise<AsyncFunction<Iterable<SourceRow>, Void>> updateMatch = projections
                .then(curry(new MatchRowFunction(binding.getMatchFields())))
                .then(curry(new UpdateMatchFunction(binding.getMatchTable())))
                .then(curry(AsyncFunctions.<SourceRow>incrementalMap()));


        // now use this function to
        getRows().then(updateMatch);
    }



    /**
     * Persists all valid, imported FormInstances
     */
    public Promise<Integer> persistInstances() {

        List<SourceRow> rows = importModel.getSource().getRows();

        CreateInstanceFunction create = new CreateInstanceFunction(
                importModel.getFormClass(),
                createFieldBindings(),
                new InstanceIdentityFunction());

        return Promise.resolved(filter(rows, isValid()))
                .then(mapReduce(compose(resources.persist(), create), count()));
    }

    private List<FieldBinding> createFieldBindings() {
        return importModel.createFieldBindings();
    }

    private ValidRowPredicate isValid() {
        return new ValidRowPredicate(createFieldBindings());
    }

    private class InstanceIdentityFunction implements Function<SourceRow, Cuid> {

        @Nullable
        @Override
        public Cuid apply(@Nullable SourceRow input) {
            Cuid siteCuid = rowIds.get(input.getRowIndex());
            if(siteCuid == null) {
                siteCuid = CuidAdapter.cuid(CuidAdapter.SITE_DOMAIN, keyGenerator.generateInt());
                rowIds.put(input.getRowIndex(), siteCuid);
            }
            return siteCuid;
        }
    }


    private class UpdateMatchFunction implements Function<Pair<Function<SourceRow, ScoredReference>, SourceRow>, Void> {

        private final MatchTable matchTable;

        private UpdateMatchFunction(MatchTable matchTable) {
            this.matchTable = matchTable;
        }

        @Nullable
        @Override
        public Void apply(@Nullable SourceRow input) {
            ScoredReference match = matchFunction.apply(input);
            matchTable.setMatch(input.getRowIndex(), match);
            return null;
        }

        @Override
        public Void apply(Pair<Function<SourceRow, ScoredReference>, SourceRow> input) {
            SourceRow row = input.getB();
            ScoredReference match = input.getA().apply(row);
            matchTable.setMatch(row.getRowIndex(), match);
            return null;
        }
    }
}
