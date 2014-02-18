package org.activityinfo.ui.full.client.importer;


import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.google.gwt.core.client.testing.StubScheduler;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.full.client.importer.columns.DraftColumn;
import org.activityinfo.ui.full.client.importer.data.PastedTable;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.draft.Draft;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Resources.*;
import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/brac-import.db.xml")
public class ImporterTest extends CommandTestCase2 {

    private static final Cuid HOUSEHOLD_SURVEY_FORM_CLASS = CuidAdapter.activityFormClass(1);

    private static final Cuid BRAC_PARTNER_CUID = CuidAdapter.partnerInstanceId(1);

    public static final int COLUMN_WIDTH = 50;



    private ResourceLocatorAdaptor resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;
    private Importer importer;
    private StubScheduler scheduler;

    @Before
    public void setupAdapters() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
        scheduler = new StubScheduler();
    }

    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(Promise.promise(HOUSEHOLD_SURVEY_FORM_CLASS, formTreeBuilder));
        dumpList("TREE NODES", formTree.search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.<FormTree.Node>alwaysTrue()));

        importer = new Importer(formTree);


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource(Importer.class, "qis.csv"), Charsets.UTF_8));
        importer.setSource(source);

        dumpList("COLUMNS", source.getColumns());

        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importer.getFieldsToMatch());
        importer.setColumnBinding(field("NumAdultMale"), columnIndex("MEMBER_NO_ADULT_FEMALE"));
        importer.setColumnBinding(field("[End Date]"), columnIndex("_SUBMISSION_DATE"));
        importer.setColumnBinding(field("Upzilla.District.Name"), columnIndex("district"));
        importer.setColumnBinding(field("Upzilla.Name"), columnIndex("upazila"));

        // Step 3: Populate draft instances
        Draft draft = new Draft(scheduler, resourceLocator, importer);
        draft.updateDrafts();

        // Step 4: Match Instances
        // For each row object field, we need to determine the range of possible/probably values
        Promise<Void> references = draft.matchReferences();
        runAll();
        assertResolves(references);

        // Step 5: Validate
        System.out.println("Valid: " + draft.getValidCount());
        System.out.println("Invalid: " + draft.getInvalidCount());

        // Step 6: Present to the user for validation / correction
        List columns = importer.getImportColumns();
        dumpHeaders(columns);
        dumpRows(columns, draft.getInstances());

        // Step 7: Users provides input to complete
        assertResolves(draft.transformField(field("Partner"), Functions.constant(BRAC_PARTNER_CUID)));


    }

    private void runAll() {
        do {
            System.out.println("Running scheduled commands...");
        } while(scheduler.executeCommands());
    }


    private void dumpHeaders(List<DraftColumn> importColumns) {

        System.out.print("  ");
        for(DraftColumn col : importColumns) {
            System.out.print("  " + Strings.padEnd(col.getHeader().getValue(), COLUMN_WIDTH - 2, ' '));
        }
        System.out.println();
        System.out.println(Strings.repeat("-", COLUMN_WIDTH * importColumns.size()));

    }

    private void dumpRows(List<DraftColumn> columns, List<DraftInstance> draftInstances) {
        for(DraftInstance instance : draftInstances) {
            System.out.print(rowIcon(instance) + " ");
            for(DraftColumn col : columns) {
                Object value = col.getValue(instance);
                String stringValue = "";
                if(value != null) {
                    stringValue = value.toString();
                    if(stringValue.length() > (COLUMN_WIDTH-2)) {
                        stringValue = stringValue.substring(0, COLUMN_WIDTH-2);
                    }
                }
                System.out.print(" " + icon(col.getStatus(instance)) + Strings.padEnd(stringValue,
                        COLUMN_WIDTH - 2, ' '));
            }
            System.out.println();
        }
    }

    private String rowIcon(DraftInstance instance) {
        if(!instance.isValid()) {
            return "x";
        } else {
            return " ";
        }
    }

    private String icon(ValueStatus status) {
        if(status == null) {
            return "?";
        }
        switch(status) {

            case OK:
                return " ";
            case WARNING:
                return "*";
            case ERROR:
                return "!";
        }
        return "?";
    }

    private FieldPath field(String debugFieldPath) {
        List<FieldPath> fieldsToMatch = importer.getFieldsToMatch();
        List<String> fieldsWeHave = Lists.newArrayList();
        for(FieldPath path : fieldsToMatch) {
            String debugPath = importer.getFormTree().getNodeByPath(path).debugPath();
            if(debugPath.equals(debugFieldPath)) {
                return path;
            }
            fieldsWeHave.add(debugPath);
        }
        throw new RuntimeException(String.format("No field matching '%s', we have: %s",
                debugFieldPath, fieldsWeHave));
    }

    private int columnIndex(String header) {
        for(SourceColumn column : importer.getSource().getColumns()) {
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
