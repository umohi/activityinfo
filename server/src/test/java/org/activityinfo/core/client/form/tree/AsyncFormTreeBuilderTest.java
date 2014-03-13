package org.activityinfo.core.client.form.tree;

import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.core.client.PromiseMatchers.resolvesTo;
import static org.junit.Assert.assertThat;


@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class AsyncFormTreeBuilderTest extends CommandTestCase2 {

    @Test
    public void treeResolver() {
        ResourceLocator locator = new ResourceLocatorAdaptor(getDispatcher());
        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(locator);
        Promise<FormTree> tree = treeBuilder.apply(CuidAdapter.activityFormClass(1));

        System.out.println(assertResolves(tree).toString());

        assertThat(tree, resolvesTo(CoreMatchers.<FormTree>notNullValue()));
    }
}
