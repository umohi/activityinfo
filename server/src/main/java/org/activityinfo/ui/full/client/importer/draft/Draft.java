package org.activityinfo.ui.full.client.importer.draft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.api2.client.Action;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.Importer;
import org.activityinfo.ui.full.client.importer.columns.DataColumn;
import org.activityinfo.ui.full.client.importer.columns.DraftColumn;
import org.activityinfo.ui.full.client.importer.columns.MissingFieldColumn;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.ui.full.client.importer.match.ReferenceMatcher;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by alex on 2/14/14.
 */
public class Draft {

    private ResourceLocator resourceLocator;
    private final Importer importModel;
    private List<DraftInstance> drafts = Lists.newArrayList();

    public Draft(ResourceLocator resourceLocator, Importer importer) {
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
        Set<Cuid> referenceFields = Sets.newHashSet();
        Set<FieldPath> mappedFieldPaths = importModel.getMappedFieldPaths();

        for(FieldPath mappedPath : mappedFieldPaths) {
            if(mappedPath.isNested()) {
                referenceFields.add(mappedPath.getRoot());
            }
        }

        Promise<Void> promise = Promise.resolved(null);

        for(Cuid referenceFieldId : referenceFields) {
            FormTree.Node referenceFieldNode = importModel.getFormTree().getNodeByPath(
                    new FieldPath(referenceFieldId));

            final ReferenceMatcher matcher = new ReferenceMatcher(referenceFieldNode, mappedFieldPaths);

            promise = promise
            .then(new AsyncFunction<Void, List<Projection>>() {
                @Override
                public Promise<List<Projection>> apply(@Nullable Void aVoid) {
                    return resourceLocator.query(matcher.getProjectedFields(), matcher.getRange());
                }
            })
            .then(new Action<List<Projection>>() {
                @Override
                public void execute(List<Projection> input) {
                    matcher.match(drafts, input);
                }
            });
        }

        return promise;
    }
}
