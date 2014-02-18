package org.activityinfo.api2.client.form.tree;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api2.client.AsyncFunction;
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
public class AsyncFormTreeBuilder implements AsyncFunction<Cuid, FormTree> {

    private static final Logger LOGGER = Logger.getLogger(AsyncFormTreeBuilder.class.getName());

    private final ResourceLocator locator;

    public AsyncFormTreeBuilder(ResourceLocator locator) {
        this.locator = locator;
    }

    @Override
    public void apply(Cuid formClassId, AsyncCallback<FormTree> callback) {
        new Resolver(formClassId, callback);
    }

    public class Resolver {

        private AsyncCallback<? super FormTree> callback;
        private FormTree tree;
        private int outstandingRequests = 0;

        public Resolver(final Cuid formClassId, final AsyncCallback<FormTree> callback) {
            this.callback = callback;
            locator.getFormClass(formClassId).then(new AsyncCallback<FormClass>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(FormClass result) {
                    tree = new FormTree(result);
                    requestFormClassForNode(tree.getRoot(), formClassId);
                }
            });
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
            node.addChildren(formClass);
            for(FormTree.Node child : node.getChildren()) {
                if(child.isReference()) {
                    for(Iri rangeClass : child.getRange()) {
                        if(rangeClass.getScheme().equals(Cuids.SCHEME)) {
                            requestFormClassForNode(child, new Cuid(rangeClass.getSchemeSpecificPart()));
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
