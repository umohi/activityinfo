package org.activityinfo.ui.client.importer;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.activityinfo.core.server.type.converter.JvmConverterFactory;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.*;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.strategy.FieldImportStrategies;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/nfi-import.db.xml")
public class ImportWithMultiClassRangeTest extends AbstractImporterTest {

    public static final Cuid NFI_DISTRIBUTION_FORM_CLASS = CuidAdapter.activityFormClass(33);

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

        importModel.setColumnAction(columnIndex("Date1"), target("End Date"));
        importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));
        importModel.setColumnAction(columnIndex("Localité"), target("Localité Name"));
        importModel.setColumnAction(columnIndex("Province"), target("Province Name"));
        importModel.setColumnAction(columnIndex("District"), target("District Name"));
        importModel.setColumnAction(columnIndex("Territoire"), target("Territoire Name"));
        importModel.setColumnAction(columnIndex("Territoire"), target("Territoire Name"));
        importModel.setColumnAction(columnIndex("Secteur"), target("Secteur Name"));
        importModel.setColumnAction(columnIndex("Groupement"), target("Groupement Name"));
        importModel.setColumnAction(columnIndex("Zone de Santé"), target("Zone de Santé Name"));
        importModel.setColumnAction(columnIndex("Nombre de ménages ayant reçu une assistance en NFI"), target("Nombre de ménages ayant reçu une assistance en NFI"));


        matchReferences();
        validateRows();
//        showValidationGrid();


    }
}
