package org.activityinfo.ui.client.importer;


import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.io.Resources;
import org.activityinfo.core.server.formatter.JavaTextQuantityFormatterFactory;
import org.activityinfo.core.server.type.converter.JvmConverterFactory;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.activityinfo.ui.client.component.importDialog.validation.cells.ValidationCellTemplatesStub;
import org.activityinfo.ui.client.component.importDialog.validation.columns.ColumnFactory;
import org.activityinfo.ui.client.component.importDialog.validation.columns.ImportColumn;
import org.activityinfo.ui.client.component.importDialog.validation.columns.MissingReferenceColumn;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/brac-import.db.xml")
public class ImporterTest extends AbstractImporterTest {

    private static final Cuid HOUSEHOLD_SURVEY_FORM_CLASS = CuidAdapter.activityFormClass(1);

    private static final Cuid BRAC_PARTNER_CUID = CuidAdapter.partnerInstanceId(1);

    public static final int MODHUPUR = 24;


    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        dumpList("TREE NODES", formTree.search(FormTree.SearchOrder.BREADTH_FIRST,
                Predicates.alwaysTrue(), Predicates.<FormTree.Node>alwaysTrue()));

        importModel = new ImportModel(formTree);


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));
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
        Importer importer = new Importer(scheduler, resourceLocator, JvmConverterFactory.get(), importModel);

        importer.updateBindings();
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
        assertResolves(importer.persistInstances());


        // AND... verify
        Filter filter = new Filter();
        filter.addRestriction(DimensionType.AdminLevel, MODHUPUR);

        SiteResult sites = execute(new GetSites(filter));
        assertThat(sites.getTotalLength(), equalTo(1));

        SiteDTO site = sites.getData().get(0);
        assertThat(site.getDate1(), equalTo(new LocalDate(2012,12,19)));
        assertThat(site.getDate2(), equalTo(new LocalDate(2012,12,19)));

    }
}
