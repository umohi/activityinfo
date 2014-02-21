package org.activityinfo.ui.full.client.importer;


import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.safehtml.shared.SafeHtml;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.api2.server.form.JavaTextQuantityFormatterFactory;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.full.client.importer.binding.FieldBinding;
import org.activityinfo.ui.full.client.importer.data.SourceRow;
import org.activityinfo.ui.full.client.importer.data.PastedTable;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.match.*;
import org.activityinfo.ui.full.client.importer.ui.Importer;
import org.activityinfo.ui.full.client.importer.ui.validation.cells.ValidationCellTemplatesStub;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ColumnFactory;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.ImportColumn;
import org.activityinfo.ui.full.client.importer.ui.validation.columns.MissingReferenceColumn;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.*;
import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/brac-import.db.xml")
public class ImporterTest extends CommandTestCase2 {

    private static final Cuid HOUSEHOLD_SURVEY_FORM_CLASS = CuidAdapter.activityFormClass(1);

    private static final Cuid BRAC_PARTNER_CUID = CuidAdapter.partnerInstanceId(1);

    public static final int COLUMN_WIDTH = 50;


    private ResourceLocatorAdaptor resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;
    private ImportModel importModel;
    private StubScheduler scheduler;
    private NumberFormat numberFormat;

    @Before
    public void setupAdapters() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
        scheduler = new StubScheduler();

        // disable GWT.create so that references in static initializers
        // don't sink our test

        GWTMockUtilities.disarm();
    }

    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        dumpList("TREE NODES", formTree.search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.<FormTree.Node>alwaysTrue()));

        importModel = new ImportModel(formTree);


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource(ImportModel.class, "qis.csv"), Charsets.UTF_8));
        importModel.setSource(source);

        dumpList("COLUMNS", source.getColumns());

        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importModel.getFieldsToMatch());
        importModel.setColumnBinding(field("NumAdultMale"), columnIndex("MEMBER_NO_ADULT_FEMALE"));
        importModel.setColumnBinding(field("[End Date]"), columnIndex("_SUBMISSION_DATE"));
        importModel.setColumnBinding(field("Upzilla.District.Name"), columnIndex("district"));
        importModel.setColumnBinding(field("Upzilla.Name"), columnIndex("upazila"));

        // Step 3: Match Instances
        // For each row object field, we need to determine the range of possible/probably values
        Importer importer = new Importer(scheduler, resourceLocator, importModel);
        runScheduledAndAssertResolves(importer.matchReferences());

        // Step 4: Validate
        Integer validCount = runScheduledAndAssertResolves(importer.countValidRows());
        System.out.println("VALID ROWS: " + validCount);

        // Step 5: Present to the user for validation / correction
        ColumnFactory columnFactory = new ColumnFactory(
                new JavaTextQuantityFormatterFactory(),
                new ValidationCellTemplatesStub(),
                importModel.getFormTree());

        List<ImportColumn<?>> columns = columnFactory.create(importer.getBindings());

        dumpHeaders(columns);
        dumpRows(columns, importModel.getSource().getRows());

        // Step 6: Users provides input to complete
        MissingReferenceColumn partnerColumn = (MissingReferenceColumn) columns.get(0);
        partnerColumn.getBinding().setProvidedValue(BRAC_PARTNER_CUID);

        validCount = runScheduledAndAssertResolves(importer.countValidRows());
        assertThat(validCount, equalTo(importModel.getSource().getRows().size()));

        // Step 7: Import!!!!



    }

    private <T> T runScheduledAndAssertResolves(Promise<T> promise) {
        runAll();
        return assertResolves(promise);
    }

    private void runAll() {
        while(scheduler.executeCommands()) {}
    }


    private void dumpHeaders(List<ImportColumn<?>> importColumns) {

        System.out.print("  ");
        for(ImportColumn col : importColumns) {
            System.out.print("  " + Strings.padEnd(col.getHeader(), COLUMN_WIDTH - 2, ' '));
        }
        System.out.println();
        System.out.println(Strings.repeat("-", COLUMN_WIDTH * importColumns.size()));

    }

    private void dumpRows(List<ImportColumn<?>> columns, List<SourceRow> rows) {
        for(SourceRow instance : rows) {
            System.out.print(rowIcon(instance) + " ");
            for(ImportColumn<?> col : columns) {
                Object value = col.getValue(instance);
                String stringValue = "";
                if(value != null) {
                    if(value instanceof SafeHtml) {
                        stringValue = ((SafeHtml) value).asString();
                    } else {
                        stringValue = value.toString();
                    }
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

    private String rowIcon(SourceRow instance) {
//        if(!instance.isValid()) {
//            return "x";
//        } else {
//            return " ";
//        }
        return " ";
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
        List<FieldPath> fieldsToMatch = importModel.getFormTree().search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.alwaysTrue());

        List<String> fieldsWeHave = Lists.newArrayList();
        for(FieldPath path : fieldsToMatch) {
            String debugPath = importModel.getFormTree().getNodeByPath(path).debugPath();
            if(debugPath.equals(debugFieldPath)) {
                return path;
            }
            fieldsWeHave.add(debugPath);
        }
        throw new RuntimeException(String.format("No field matching '%s', we have: %s",
                debugFieldPath, fieldsWeHave));
    }

    private int columnIndex(String header) {
        for(SourceColumn column : importModel.getSource().getColumns()) {
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
