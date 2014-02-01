package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.base.Joiner;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.IndicatorDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.ui.full.client.importer.data.PastedImportSource;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;
import org.activityinfo.ui.full.client.importer.schema.ActivityClass;
import org.activityinfo.ui.full.client.importer.schema.IndicatorClass;
import org.activityinfo.ui.full.client.importer.schema.IndicatorImporter;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImportModelTest {

    @Ignore("wip") @Test
    public void test() {

        String tableData = "Activity,Indictator Name,Units\n" +
                "NFI Distribution,Number of households,households\n" +
                "School Reconstruction,Number of classrooms,classrooms\n" +
                "Awareness raising,Number of benes,people\n";

        UserDatabaseDTO database = new UserDatabaseDTO(1, "RDC");
        database.getActivities().add(new ActivityDTO(1, "NFI Distribution"));
        database.getActivities().add(new ActivityDTO(2, "School Reconstruction"));

        IndicatorImporter importer = new IndicatorImporter(null, database);
        ImportModel<IndicatorDTO> importModel = new ImportModel<IndicatorDTO>(importer);
        importModel.setSource(new PastedImportSource(tableData));

        importModel.setColumnBinding(new PropertyPath(IndicatorClass.ACTIVITY, ActivityClass.NAME), 0);
        importModel.setColumnBinding(new PropertyPath(IndicatorClass.NAME), 1);
        importModel.setColumnBinding(new PropertyPath(IndicatorClass.UNITS), 2);

        importModel.updateDrafts();

        System.out.println(Joiner.on("\n").join(importModel.getDraftModels()));

        InstanceMatch activity = (InstanceMatch) importModel.getDraftModels().get(0).getValue(".activity");

        assertThat(activity.isNewInstance(), equalTo(false));
        assertThat(activity.getInstanceId(), equalTo("1"));

    }


}
