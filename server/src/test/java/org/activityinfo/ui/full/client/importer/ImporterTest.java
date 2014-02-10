package org.activityinfo.ui.full.client.importer;


import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.io.Resources;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.api2.client.PromiseMatchers;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.full.client.importer.binding.ClassMatcherSet;
import org.activityinfo.ui.full.client.importer.data.ImportColumnDescriptor;
import org.activityinfo.ui.full.client.importer.data.PastedImportSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.*;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/brac-import.db.xml")
public class ImporterTest extends CommandTestCase2 {

    private static final Cuid HOUSEHOLD_SURVEY_FORM_CLASS = CuidAdapter.activityFormClass(1);


    private ResourceLocatorAdaptor resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;
    private Importer importer;

    @Before
    public void setupAdapters() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
    }

    @Test
    public void test() throws IOException {

        FormTree formTree = PromiseMatchers.assertResolves(formTreeBuilder.build(HOUSEHOLD_SURVEY_FORM_CLASS));
        dumpList("TREE NODES", formTree.search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.<FormTree.Node>alwaysTrue()));

        importer = new Importer(formTree);


        // Step 1: User pastes in data to import
        PastedImportSource source = new PastedImportSource(
                Resources.toString(getResource(Importer.class, "qis.csv"), Charsets.UTF_8));
        importer.setSource(source);

        dumpList("COLUMNS", source.getColumns());


        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importer.getFieldsToMatch());
        importer.setColumnBinding(field("NumAdultMale"), columnIndex("MEMBER_NO_ADULT_FEMALE"));
        importer.setColumnBinding(field("[End Date]"), columnIndex("_SUBMISSION_DATE"));
        importer.setColumnBinding(field("localite.Upzilla.District.Division.Name"), columnIndex("district"));
        importer.setColumnBinding(field("localite.Upzilla.Name"), columnIndex("upazila"));

        // Step 3: Match Instances
        // For each row object field, we need to determine the range of possible/probably values
        ClassMatcherSet matcherSet = new ClassMatcherSet(formTree, importer.getColumnBindings());

        matcherSet.match(importer.getSource());

    }

    private FieldPath field(String debugFieldPath) {
        List<FieldPath> fieldsToMatch = importer.getFieldsToMatch();
        for(FieldPath path : fieldsToMatch) {
            if(path.toString().equals(debugFieldPath)) {
                return path;
            }
        }
        throw new RuntimeException(String.format("No field matching '%s', we have: %s",
                debugFieldPath, fieldsToMatch));
    }

    private int columnIndex(String header) {
        for(ImportColumnDescriptor column : importer.getSource().getColumns()) {
            if(column.getHeader().equals(header)) {
                return column.getIndex();
            }
        }
        throw new RuntimeException("No imported column with header " + header);
    }

    private void dumpList(final String title, Iterable<?> items) {
        System.out.println(title + ":");
        System.out.println("-----------------------");
        System.out.println(Joiner.on("\n").join(items));
        System.out.println("-----------------------");
        System.out.println();
    }

}
