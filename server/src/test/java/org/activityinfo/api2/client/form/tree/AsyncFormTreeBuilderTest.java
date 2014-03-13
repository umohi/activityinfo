package org.activityinfo.api2.client.form.tree;

import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;
import static org.activityinfo.api2.client.PromiseMatchers.resolvesTo;
import static org.hamcrest.CoreMatchers.notNullValue;
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
