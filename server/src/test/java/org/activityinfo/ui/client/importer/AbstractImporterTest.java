package org.activityinfo.ui.client.importer;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.safehtml.shared.SafeHtml;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.SourceColumn;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.match.ValueStatus;
import org.activityinfo.core.shared.importing.model.ColumnTarget;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.ui.client.component.importDialog.mapping.FieldChoicePresenter;
import org.activityinfo.ui.client.component.importDialog.mapping.FieldModel;
import org.activityinfo.ui.client.component.importDialog.validation.columns.ImportColumn;
import org.junit.Before;

import java.util.List;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;

public class AbstractImporterTest extends CommandTestCase2 {
    public static final int COLUMN_WIDTH = 50;
    protected ResourceLocatorAdaptor resourceLocator;
    protected AsyncFormTreeBuilder formTreeBuilder;
    protected ImportModel importModel;
    protected StubScheduler scheduler;

    @Before
    public void setupAdapters() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
        scheduler = new StubScheduler();

        // disable GWT.create so that references in static initializers
        // don't sink our test

        GWTMockUtilities.disarm();
    }

    protected <T> T runScheduledAndAssertResolves(Promise<T> promise) {
        runAll();
        return assertResolves(promise);
    }

    private void runAll() {
        while(scheduler.executeCommands()) {}
    }

    protected void dumpHeaders(List<ImportColumn<?>> importColumns) {

        System.out.print("  ");
        for(ImportColumn col : importColumns) {
            System.out.print("  " + Strings.padEnd(col.getHeader(), COLUMN_WIDTH - 2, ' '));
        }
        System.out.println();
        System.out.println(Strings.repeat("-", COLUMN_WIDTH * importColumns.size()));

    }

    protected void dumpRows(List<ImportColumn<?>> columns, List<SourceRow> rows) {
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

    protected ColumnTarget field(String debugFieldPath) {

        FieldChoicePresenter choicePresenter = new FieldChoicePresenter(importModel);


        List<String> fieldsWeHave = Lists.newArrayList();
        for(FieldModel path : choicePresenter.getOptions()) {
            if(path.getLabel().equals(debugFieldPath)) {
                return ColumnTarget.mapped(path.getTarget().getFieldPath());
            }
            fieldsWeHave.add(path.getLabel());
        }
        throw new RuntimeException(String.format("No field matching '%s', we have: %s",
                debugFieldPath, fieldsWeHave));
    }

    protected int columnIndex(String header) {
        for(SourceColumn column : importModel.getSource().getColumns()) {
            if(column.getHeader().equals(header)) {
                return column.getIndex();
            }
        }
        throw new RuntimeException("No imported column with header " + header);
    }

    protected void dumpList(final String title, Iterable<?> items) {
        System.out.println(title + ":");
        System.out.println("-----------------------");
        System.out.println(Joiner.on("\n").join(items));
        System.out.println("-----------------------");
        System.out.println();
    }
}
