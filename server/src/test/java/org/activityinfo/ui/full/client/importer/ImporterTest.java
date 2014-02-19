package org.activityinfo.ui.full.client.importer;


import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gwt.core.client.testing.StubScheduler;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.client.promises.AsyncFunctions;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.binding.MappedReferenceFieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.process.AsyncReferenceMatcher;
import org.activityinfo.ui.full.client.importer.process.CountValidRows;
import org.activityinfo.ui.full.client.importer.data.PastedTable;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.*;
import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;
import static org.junit.Assert.assertThat;

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

        List<FieldBinding> bindings = importer.createFieldBindings();

        // Step 3: Match Instances
        // For each row object field, we need to determine the range of possible/probably values

        List<AsyncFunction<Void, Void>> tasks = Lists.newArrayList();
        List<MappedReferenceFieldBinding> referenceBindings = Lists.newArrayList();
        AsyncReferenceMatcher referenceMatcher = new AsyncReferenceMatcher(
                scheduler,
                resourceLocator,
                importer.getSource());

        for(FieldBinding binding : bindings) {
            if(binding instanceof MappedReferenceFieldBinding) {
                tasks.add(AsyncFunctions.apply((MappedReferenceFieldBinding) binding, referenceMatcher));
            }
        }

        Promise<Void> matching = new Promise<>();
        AsyncFunctions.sequence(tasks).apply(null, matching);

        runAll();
        assertResolves(matching);

        // Step 5: Validate
        CountValidRows count = new CountValidRows(bindings);
        for(SourceRow row : importer.getSource().getRows()) {
            count.apply(row);
        }
        System.out.println("Valid: " + count.getValidCount());
        System.out.println("Invalid: " + count.getInvalidCount());

        // Step 6: Present to the user for validation / correction
//        List columns = importer.createFieldBindings();
//        dumpHeaders(columns);
//        dumpRows(columns, draft.getInstances());
//
//        // Step 7: Users provides input to complete
//        Promise<Void> transformPromise = draft.transformField(field("Partner"), Functions.constant(BRAC_PARTNER_CUID));
//        runAll();
//        assertResolves(transformPromise);
//
//        System.out.println("AFTER FIX");
//
//        dumpHeaders(columns);
//        dumpRows(columns, draft.getInstances());
//
//        // everything valid?
//        // IMPORT !!
//        assertThat(draft.getValidCount(), equalTo(100));
//        assertThat(draft.getInvalidCount(), equalTo(0));




    }

    private void runAll() {
        while(scheduler.executeCommands()) {}
    }

//
//    private void dumpHeaders(List<ImportColumn> importColumns) {
//
//        System.out.print("  ");
//        for(ImportColumn col : importColumns) {
//            System.out.print("  " + Strings.padEnd(col.getHeader().getValue(), COLUMN_WIDTH - 2, ' '));
//        }
//        System.out.println();
//        System.out.println(Strings.repeat("-", COLUMN_WIDTH * importColumns.size()));
//
//    }
//
//    private void dumpRows(List<ImportColumn> columns, List<DraftInstance> draftInstances) {
//        for(DraftInstance instance : draftInstances) {
//            System.out.print(rowIcon(instance) + " ");
//            for(ImportColumn col : columns) {
//                Object value = col.getValue(instance);
//                String stringValue = "";
//                if(value != null) {
//                    stringValue = value.toString();
//                    if(stringValue.length() > (COLUMN_WIDTH-2)) {
//                        stringValue = stringValue.substring(0, COLUMN_WIDTH-2);
//                    }
//                }
//                System.out.print(" " + icon(col.getStatus(instance)) + Strings.padEnd(stringValue,
//                        COLUMN_WIDTH - 2, ' '));
//            }
//            System.out.println();
//        }
//    }

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
        List<FieldPath> fieldsToMatch = importer.getFormTree().search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.alwaysTrue());

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
