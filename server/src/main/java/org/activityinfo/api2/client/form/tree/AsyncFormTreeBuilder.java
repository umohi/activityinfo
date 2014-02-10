package org.activityinfo.api2.client.form.tree;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.tree.FormTree;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Builds a {@link FormTree}
 */
public class AsyncFormTreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(AsyncFormTreeBuilder.class.getName());

    private final ResourceLocator locator;

    public AsyncFormTreeBuilder(ResourceLocator locator) {
        this.locator = locator;
    }

    public Promise<FormTree> build(final Cuid formClassId) {
        return new Promise<>(new Promise.AsyncOperation<FormTree>() {
            @Override
            public void start(Promise<FormTree> promise) {
                new Resolver(promise, formClassId);
            }
        });
    }

    public class Resolver {

        private Promise<FormTree> promise;
        private FormTree tree;
        private int outstandingRequests = 0;

        public Resolver(Promise<FormTree> promise, Cuid formClassId) {
            this.promise = promise;
            this.tree = new FormTree();

            requestFormClassForNode(tree.getRoot(), formClassId);
        }

        private void requestFormClassForNode(final FormTree.Node node, final Cuid formClassId) {

            LOGGER.fine("Requesting form class for " + node);

            outstandingRequests++;
            locator.getFormClass(formClassId).then(new AsyncCallback<FormClass>() {
                @Override
                public void onFailure(Throwable caught) {
                    Resolver.this.promise.reject(caught);
                }

                @Override
                public void onSuccess(FormClass formClass) {
                    addChildrenToNode(node, formClass);

                    outstandingRequests--;
                    if(outstandingRequests == 0) {
                        promise.resolve(tree);
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
            node.addChildren(formClass);
            for(FormTree.Node child : node.getChildren()) {
                if(child.isReference()) {
                    requestFormClassForNode(child, formIdFromRange(child.getRange()));
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
