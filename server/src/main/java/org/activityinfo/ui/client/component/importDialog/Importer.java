package org.activityinfo.ui.client.component.importDialog;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.Scheduler;
import org.activityinfo.core.client.CuidGenerator;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.Resources;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.SourceColumn;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.binding.*;
import org.activityinfo.core.shared.importing.match.ScoredReference;
import org.activityinfo.core.shared.importing.model.ColumnTarget;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.process.CreateInstanceFunction;
import org.activityinfo.core.shared.importing.process.MatchRowFunction;
import org.activityinfo.core.shared.importing.process.ValidRowPredicate;
import org.activityinfo.core.shared.type.converter.ConverterFactory;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Iterables.filter;

/**
 * Client-side importer that is able to work offline
 */
public class Importer {

    private ConverterFactory converterFactory;
    private List<FieldBinding> bindings = Lists.newArrayList();
    private Scheduler scheduler;
    private Resources resources;
    private ImportModel importModel;
    private KeyGenerator keyGenerator = new KeyGenerator();

    private Map<Integer, Cuid> rowIds = Maps.newHashMap();

    public Importer(Scheduler scheduler,
                    ResourceLocator resourceLocator,
                    ConverterFactory converterFactory,
                    ImportModel importModel) {
        this.scheduler = scheduler;

        this.converterFactory = converterFactory;
        this.resources = new Resources(resourceLocator);
        this.importModel = importModel;
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

    public FormTree getFormTree() {
        return importModel.getFormTree();
    }


    /**
     * Updates the set of field bindings based on the mappings selected by the user
     */
    public void updateBindings() {
        this.bindings = Lists.newArrayList();

        // Add Reference fields that have been mapped
        for(FieldPath referenceFieldPath : importModel.getMappedReferenceFields()) {
            bindings.add(new MappedReferenceFieldBinding(
                    getNodeByPath(referenceFieldPath),
                    importModel.getColumnBindings()));
        }

        // Add simple data fields that have been mapped, along with new fields
        for(Map.Entry<Integer, ColumnTarget> binding : importModel.getColumnBindings().entrySet()) {
            if(binding.getValue().isMapped()) {
                if(!binding.getValue().getFieldPath().isNested()) {
                    FormTree.Node fieldNode = getNodeByPath(binding.getValue().getFieldPath());
                    bindings.add(new MappedDataFieldBinding(fieldNode.getField(), binding.getKey(),
                            converterFactory.createStringConverter(fieldNode.getFieldType())));
                }
            } else if(binding.getValue().getAction() == ColumnTarget.Action.NEW_FIELD) {
                // create a new field
                SourceColumn column = importModel.getSourceColumn(binding.getKey());
                FormField field = createNewField(column);
                MappedDataFieldBinding fieldBinding = new MappedDataFieldBinding(field, binding.getKey(),
                        converterFactory.createStringConverter(field.getType()));
                fieldBinding.setNewField(true);
                bindings.add(fieldBinding);
            }
        }

        // Finally add any missing fields

        Set<Cuid> mappedRootFields = importModel.getMappedRootFields();

        for(FormTree.Node node : importModel.getFormTree().getRootFields()) {
            if(!mappedRootFields.contains(node.getFieldId()) && node.getField().isRequired()) {
                bindings.add(new MissingFieldBinding(node));
            }
        }
    }

    private FormTree.Node getNodeByPath(FieldPath referenceFieldPath) {
        return importModel.getFormTree().getNodeByPath(referenceFieldPath);
    }

    private FormField createNewField(SourceColumn column) {
        CuidGenerator generator = new CuidGenerator();
        FormField field = new FormField(generator.nextCuid());
        field.setLabel(new LocalizedString(column.getHeader()));
        field.setRequired(false);
        field.setType(FormFieldType.FREE_TEXT);
        field.setVisible(true);
        return field;
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
                getFormTree().getRootFormClasses(),
                bindings,
                new InstanceIdentityFunction());

        return Promise.applyAll(filter(rows, isValid()), compose(resources.persist(), create));
    }

    private ValidRowPredicate isValid() {
        return new ValidRowPredicate(bindings);
    }

    public List<? extends SourceRow> getRows() {
        return importModel.getSource().getRows();
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
