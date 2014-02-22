package org.activityinfo.ui.full.client.importer.ui;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.Scheduler;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.client.Resources;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MatchTable;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.match.ScoredReference;
import org.activityinfo.ui.full.client.importer.process.CreateInstanceFunction;
import org.activityinfo.ui.full.client.importer.process.MatchRowFunction;
import org.activityinfo.ui.full.client.importer.process.ValidRowPredicate;
import org.activityinfo.api.client.KeyGenerator;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Iterables.filter;

/**
 * Client-side importer that is able to work offline
 */
public class Importer {

    private final ResourceLocator resourceLocator;
    private final List<FieldBinding> bindings;
    private Scheduler scheduler;
    private Resources resources;
    private ImportModel importModel;
    private KeyGenerator keyGenerator = new KeyGenerator();

    private Map<Integer, Cuid> rowIds = Maps.newHashMap();

    public Importer(Scheduler scheduler,
                    ResourceLocator resourceLocator,
                    ImportModel importModel) {
        this.scheduler = scheduler;

        this.resourceLocator = resourceLocator;
        this.resources = new Resources(resourceLocator);
        this.importModel = importModel;
        this.bindings = importModel.createFieldBindings();
    }

    public List<FieldBinding> getBindings() {
        return bindings;
    }

    private List<MappedReferenceFieldBinding> getMappedReferenceFieldBindings() {
        List<MappedReferenceFieldBinding> bindings = Lists.newArrayList();
        for(FieldBinding binding : this.bindings) {
            if(binding instanceof MappedReferenceFieldBinding) {
                bindings.add((MappedReferenceFieldBinding) binding);
            }
        }
        return bindings;
    }

    public Promise<Integer> countValidRows() {
        return Promise.resolved(Iterables.size(Iterables.filter(importModel.getSource().getRows(), isValid())));
    }

    /**
     * Asynchronously matches imported fields against potential FormInstances to find the best
     * matches.
     */
    public Promise<Void> matchReferences() {
        return Promise.applyAll(getMappedReferenceFieldBindings(), new MatchFieldFunction());
    }


    /**
     * Persists all valid, imported FormInstances
     */
    public Promise<Void> persistInstances() {

        List<SourceRow> rows = importModel.getSource().getRows();

        CreateInstanceFunction create = new CreateInstanceFunction(
                importModel.getFormClass(),
                bindings,
                new InstanceIdentityFunction());

        return Promise.applyAll(filter(rows, isValid()), compose(resources.persist(), create));
    }

    private ValidRowPredicate isValid() {
        return new ValidRowPredicate(bindings);
    }

    private class InstanceIdentityFunction implements Function<SourceRow, Cuid> {

        @Nullable
        @Override
        public Cuid apply(SourceRow input) {
            Cuid siteCuid = rowIds.get(input.getRowIndex());
            if(siteCuid == null) {
                siteCuid = CuidAdapter.cuid(CuidAdapter.SITE_DOMAIN, keyGenerator.generateInt());
                rowIds.put(input.getRowIndex(), siteCuid);
            }
            return siteCuid;
        }
    }

    private class MatchFieldFunction implements Function<MappedReferenceFieldBinding, Promise<Void>> {
        @Override
        public Promise<Void> apply(MappedReferenceFieldBinding binding) {
            // get the list of possible matches for this REFERENCE field
            return resources
                    .query(binding.queryPotentialMatches())
                    .join(new UpdateMatchesFunction(binding));
        }
    }

    private class UpdateMatchesFunction implements Function<List<Projection>, Promise<Void>> {

        private final MatchRowFunction matchFunction;
        private final MatchTable matchTable;

        private UpdateMatchesFunction(MappedReferenceFieldBinding binding) {
            this.matchFunction = new MatchRowFunction(binding.getMatchFields());
            this.matchTable = binding.getMatchTable();
        }

        @Override
        public Promise<Void> apply(final List<Projection> projections) {
            final Promise<Void> promise = new Promise<>();
            final Iterator<SourceRow> iterator = importModel.getSource().getRows().iterator();
            scheduler.scheduleIncremental(new Scheduler.RepeatingCommand() {
                @Override
                public boolean execute() {
                    if(iterator.hasNext())  {
                        try {
                            SourceRow row = iterator.next();
                            ScoredReference match = matchFunction.apply(projections, row);
                            matchTable.setMatch(row.getRowIndex(), match);
                            return true;
                        } catch(Throwable caught) {
                            promise.reject(caught);
                            return false;
                        }
                    } else {
                        promise.resolve(null);
                        return false;
                    }
                }
            });
            return promise;
        }
    }
}
