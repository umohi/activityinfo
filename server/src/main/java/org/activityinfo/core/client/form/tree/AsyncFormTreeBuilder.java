package org.activityinfo.core.client.form.tree;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Cuids;
import org.activityinfo.core.shared.Iri;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.fp.client.Promise;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Builds a {@link FormTree}
 */
public class AsyncFormTreeBuilder implements Function<Cuid, Promise<FormTree>> {

    private static final Logger LOGGER = Logger.getLogger(AsyncFormTreeBuilder.class.getName());

    private final ResourceLocator locator;

    public AsyncFormTreeBuilder(ResourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public Promise<FormTree> apply(Cuid formClassId) {
        return execute(Collections.singleton(formClassId));
    }

    public Promise<FormTree> execute(Iterable<Cuid> formClasses) {
        Promise<FormTree> result = new Promise<>();
        new Resolver(formClasses, result);
        return result;
    }

    public class Resolver {

        private AsyncCallback<? super FormTree> callback;
        private FormTree tree;
        private int outstandingRequests = 0;

        public Resolver(final Iterable<Cuid> classIds, final AsyncCallback<FormTree> callback) {
            this.callback = callback;
            this.tree = new FormTree();
            for(Cuid formClass : classIds) {
                requestFormClassForNode(null, formClass);
            }
        }

        private void requestFormClassForNode(final FormTree.Node node, final Cuid formClassId) {

            LOGGER.fine("Requesting form class for " + node);

            outstandingRequests++;
            locator.getFormClass(formClassId).then(new AsyncCallback<FormClass>() {
                @Override
                public void onFailure(Throwable caught) {
                    Resolver.this.callback.onFailure(caught);
                }

                @Override
                public void onSuccess(FormClass formClass) {
                    addChildrenToNode(node, formClass);

                    outstandingRequests--;
                    if(outstandingRequests == 0) {
                        callback.onSuccess(tree);
                    }
                }
            });
        }

        /**
         * Now that we have the actual FormClass model that corresponds to this node's
         * formClassId, add it's children.
         *
         * @param node
         * @param formClass
         */
        private void addChildrenToNode(FormTree.Node node, FormClass formClass) {
            if(node == null) {
                tree.addRootFormClass(formClass);
                queueNextRequests(tree.getRootFields());
            } else {
                node.addChildrenFromReferencedClass(formClass);
                queueNextRequests(node.getChildren());
            }
        }

        private void queueNextRequests(List<FormTree.Node> children) {
            for(FormTree.Node child : children) {
                if(child.isReference()) {
                    for(Cuid rangeClass : child.getRange()) {
                        final Iri iri = rangeClass.asIri();
                        if(iri.getScheme().equals(Cuids.SCHEME)) {
                            requestFormClassForNode(child, new Cuid(iri.getSchemeSpecificPart()));
                        }
                    }
                }
            }
        }

        /**
         * Returns a single FormClass id from a set of IRIs defining the range of the field.
         * Cheating a bit, but a field could have a range of multiple fields, but it will do for now.
         */
        private Cuid formIdFromRange(Set<Iri> range) {
            Preconditions.checkState(range.size() == 1);

            Iri rangeIri = range.iterator().next();
            Preconditions.checkState(rangeIri.getScheme().equals(Cuids.SCHEME));

            return new Cuid(rangeIri.getSchemeSpecificPart());
        }
    }
}
