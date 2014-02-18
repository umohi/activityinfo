package org.activityinfo.ui.full.client.importer.draft;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.client.promises.Action;
import org.activityinfo.api2.client.promises.AsyncFunctions;
import org.activityinfo.api2.client.promises.AsyncTask;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.Importer;
import org.activityinfo.ui.full.client.importer.match.ReferenceMatcher;

import java.util.List;
import java.util.Set;

import static org.activityinfo.api2.client.promises.AsyncFunctions.mapNonBlocking;

/**
 * Created by alex on 2/14/14.
 */
public class Draft {

    private Scheduler scheduler;
    private ResourceLocator resourceLocator;
    private final Importer importModel;
    private List<DraftInstance> drafts = Lists.newArrayList();
    private int validCount;
    private int invalidCount;

    public Draft(Scheduler scheduler, ResourceLocator resourceLocator, Importer importer) {
        this.scheduler = scheduler;
        this.resourceLocator = resourceLocator;
        this.importModel = importer;
        for(int i=0;i!=importer.getSource().getRows().size();++i) {
            drafts.add(new DraftInstance());
        }
    }

    public void updateDrafts() {
        DraftInstanceUpdater updater = new DraftInstanceUpdater(importModel);
        for(int i=0;i!=drafts.size();++i) {
            updater.updateDraft(importModel.getSource().getRows().get(i), drafts.get(i));
        }
    }

    public List<DraftInstance> getInstances() {
        return drafts;
    }


    public Promise<Void> matchReferences() {
        Set<FieldPath> referenceFields = Sets.newHashSet();

        for(FieldPath mappedPath : getMappedFieldPaths()) {
            if(mappedPath.isNested()) {
                referenceFields.add(new FieldPath(mappedPath.getRoot()));
            }
        }

        List<ReferenceMatcher> matchers = Lists.newArrayList();
        for(FieldPath referenceField : referenceFields) {
            matchers.add(newMatcher(referenceField));
        }

        return Promise.promise(matchers, AsyncFunctions.mapAction(new ReferenceFieldMatcher()));
    }

    private ReferenceMatcher newMatcher(FieldPath referenceField) {
        FormTree.Node node = importModel.getFormTree().getNodeByPath(referenceField);
        return new ReferenceMatcher(node, getMappedFieldPaths());
    }

    private Set<FieldPath> getMappedFieldPaths() {
        return importModel.getMappedFieldPaths();
    }


    public int getValidCount() {
        return validCount;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public Promise<Void> transformField(FieldPath fieldPath, Function<Object, ?> function) {

        List<AsyncTask<Void>> tasks = Lists.newArrayList();

        // apply the field transformation
        tasks.add(AsyncFunctions.apply(getInstances(),
                AsyncFunctions.mapNonBlocking(scheduler, new FieldTransformation(fieldPath, function))));

        // do we need to update reference matches?
        if(fieldPath.isNested()) {
            ReferenceMatcher matcher = newMatcher(new FieldPath(fieldPath.getRoot()));
            tasks.add(AsyncFunctions.apply(matcher, new ReferenceFieldMatcher()));
        }

        // and validate...
        tasks.add(new InstanceValidator());

        return new Promise<>(AsyncFunctions.sequence(tasks));

    }

    private class ReferenceFieldMatcher implements AsyncFunction<ReferenceMatcher, Void> {

        @Override
        public void apply(final ReferenceMatcher matcher, AsyncCallback<Void> callback) {
            resourceLocator
                .query(matcher.getProjectedFields(), matcher.getRange())
                .then(new AsyncFunction<List<Projection>, Void>() {
                    @Override
                    public void apply(List<Projection> projections, AsyncCallback<Void> callback) {
                        mapNonBlocking(scheduler, matcher.matchFunction(projections))
                                .apply(getInstances(), callback);
                    }
                });
        }
    }


    private class InstanceValidator extends AsyncTask<Void> {

        private int validCount;
        private int invalidCount;

        @Override
        protected void apply(AsyncCallback<Void> callback) {
            for(DraftInstance instance : getInstances()) {
                if(validate(instance)) {
                    validCount++;
                } else {
                    invalidCount ++;
                }
            }
            Draft.this.validCount = validCount;
            Draft.this.invalidCount = invalidCount;
            callback.onSuccess(null);
        }

        private boolean validate(DraftInstance instance) {
            boolean valid = true;
            for(FormTree.Node node : importModel.getFormTree().getRoot().getChildren()) {
                DraftFieldValue fieldValue = instance.getField(node.getPath());
                if(fieldValue.isConversionError()) {
                    valid = false;
                } else if(fieldValue.isAmbiguous()) {
                    valid = false;
                }
                if(node.getField().isRequired() && fieldValue.getMatchedValue() == null) {
                    valid = false;
                }
                if(!valid) {
                    break;
                }
            }
            instance.setValid(valid);

            return valid;
        }


    }

    private class FieldTransformation extends Action<DraftInstance> {
        private final FieldPath fieldPath;
        private final Function<Object, ?> function;

        private FieldTransformation(FieldPath fieldPath, Function<Object, ?> function) {
            this.fieldPath = fieldPath;
            this.function = function;
        }

        @Override
        public void execute(DraftInstance input) {
            DraftFieldValue value = input.getField(fieldPath);
            value.setImportedValue( function.apply(value.getImportedValue()) );
        }
    }


}
