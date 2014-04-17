package org.activityinfo.ui.client.importer;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.*;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.server.database.OnDataSet;
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

//        Hierarchy hierarchy = new Hierarchy(formTree.getNodeByPath(new FieldPath(CuidAdapter.locationField(33))));
//        HierarchyPrettyPrinter.prettyPrint(hierarchy);
//
//
//        ColumnChoicePresenter choicePresenter = new ColumnChoicePresenter(
//                importModel = new ImportModel(formTree));
//
//        for(FieldModel fieldModel : choicePresenter.getOptions()) {
//            System.out.println(fieldModel.getLabel());
//        }
//
//        // Step 1: User pastes in data to import
//        PastedTable source = new PastedTable(
//                Resources.toString(getResource("org/activityinfo/core/shared/importing/nfi.csv"),
//                        Charsets.UTF_8));
//        importModel.setSource(source);
//
//        dumpList("COLUMNS", source.getColumns());
//
//        importModel.setColumnBinding(field("End Date"), columnIndex("Date1"));
//        importModel.setColumnBinding(field("Partner Name"), columnIndex("Partner"));
//        importModel.setColumnBinding(field("Localité Name"), columnIndex("Localité"));
//        importModel.setColumnBinding(field("Province Name"), columnIndex("Province"));
//        importModel.setColumnBinding(field("District Name"), columnIndex("District"));
//        importModel.setColumnBinding(field("Territoire Name"), columnIndex("Territoire"));
//        importModel.setColumnBinding(field("Territoire Name"), columnIndex("Territoire"));
//        importModel.setColumnBinding(field("Secteur Name"), columnIndex("Secteur"));
//        importModel.setColumnBinding(field("Groupement Name"), columnIndex("Groupement"));
//        importModel.setColumnBinding(field("Zone de Santé Name"), columnIndex("Zone de Santé"));
//        importModel.setColumnBinding(field("Nombre de ménages ayant reçu une assistance en NFI"),
//                columnIndex("Nombre de ménages ayant reçu une assistance en NFI"));
//
//
//        matchReferences();
//        validateRows();
//        showValidationGrid();


    }
}
