package org.activityinfo.ui.client.importer;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import org.activityinfo.core.server.type.converter.JvmConverterFactory;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.*;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.strategy.FieldImportStrategies;
import org.activityinfo.core.shared.importing.validation.ValidatedRowTable;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/nfi-import.db.xml")
public class ImportWithMultiClassRangeTest extends AbstractImporterTest {

    public static final Cuid NFI_DISTRIBUTION_FORM_CLASS = CuidAdapter.activityFormClass(33);

    // admin levels
    public static final int PROVINCE_LEVEL = 1;
    public static final int DISTRICT_LEVEL = 2;
    public static final int TERRITOIRE_LEVEL = 3;
    public static final int SECTEUR_LEVEL = 4;
    public static final int GROUPEMENT_LEVEL = 5;
    public static final int ZONE_DE_SANTE = 7;
    public static final int AIRE_DE_SANTE = 8;

    // indicators
    public static final int NUMBER_MENAGES = 118;

    // attributes
    public static final int ECHO = 400;
    public static final int DEPLACEMENT = 63;

    @Test
    public void test() throws IOException {

        setUser(3);

        FormTree formTree = assertResolves(formTreeBuilder.apply(NFI_DISTRIBUTION_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        Hierarchy hierarchy = new Hierarchy(formTree.getNodeByPath(new FieldPath(CuidAdapter.locationField(33))));
        HierarchyPrettyPrinter.prettyPrint(hierarchy);

        importModel = new ImportModel(formTree);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));
//        ColumnChoicePresenter choicePresenter = new ColumnChoicePresenter(
//
//        for (FieldModel fieldModel : choicePresenter.getOptions()) {
//            System.out.println(fieldModel.getLabel());
//        }

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/nfi.csv"), Charsets.UTF_8));
        importModel.setSource(source);

        dumpList("COLUMNS", source.getColumns());

        importModel.setColumnAction(columnIndex("Date2"), target("End Date"));
        importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));
        importModel.setColumnAction(columnIndex("Localité"), target("Localité Name"));
        importModel.setColumnAction(columnIndex("Province"), target("Province Name"));
        importModel.setColumnAction(columnIndex("District"), target("District Name"));
        importModel.setColumnAction(columnIndex("Territoire"), target("Territoire Name"));
        importModel.setColumnAction(columnIndex("Secteur"), target("Secteur Name"));
        importModel.setColumnAction(columnIndex("Groupement"), target("Groupement Name"));
        importModel.setColumnAction(columnIndex("Zone de Santé"), target("Zone de Santé Name"));
        importModel.setColumnAction(columnIndex("Nombre de ménages ayant reçu une assistance en NFI"), target("Nombre de ménages ayant reçu une assistance en NFI"));

        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);

        assertResolves(importer.persist(importModel));

        GetSites query = new GetSites(Filter.filter().onActivity(33));
        query.setSortInfo(new SortInfo("date2", Style.SortDir.DESC));

        SiteResult result = execute(query);
        assertThat(result.getTotalLength(), equalTo(651));

        System.out.println(Joiner.on("\n").join(result.getData()));

        SiteDTO lastSite = result.getData().get(0);
        assertThat(lastSite.getDate2(), equalTo(new LocalDate(2013,4,30)));
        assertThat(lastSite.getLocationName(), equalTo("Kilimani Camp"));
        assertThat(lastSite.getAdminEntity(PROVINCE_LEVEL).getName(), equalTo("Nord Kivu"));
        assertThat(lastSite.getAdminEntity(DISTRICT_LEVEL).getName(), equalTo("Nord Kivu"));
        assertThat(lastSite.getAdminEntity(TERRITOIRE_LEVEL).getName(), equalTo("Masisi"));
        assertThat(lastSite.getAdminEntity(SECTEUR_LEVEL).getName(), equalTo("Masisi"));

        assertThat(lastSite.getIndicatorValue(NUMBER_MENAGES), equalTo(348.0));

        assertThat(lastSite.getAttributeValue(ECHO), equalTo(false));
        assertThat(lastSite.getAttributeValue(DEPLACEMENT), equalTo(true));
    }
}
