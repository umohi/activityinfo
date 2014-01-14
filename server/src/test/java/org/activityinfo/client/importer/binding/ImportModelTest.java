package org.activityinfo.client.importer.binding;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.activityinfo.client.importer.data.PastedImportSource;
import org.activityinfo.client.importer.ont.PropertyPath;
import org.activityinfo.client.importer.schema.ActivityClass;
import org.activityinfo.client.importer.schema.IndicatorClass;
import org.activityinfo.client.importer.schema.IndicatorImporter;
import org.activityinfo.shared.dto.ActivityDTO;
import org.activityinfo.shared.dto.IndicatorDTO;
import org.activityinfo.shared.dto.UserDatabaseDTO;
import org.junit.Test;

import com.google.common.base.Joiner;

public class ImportModelTest {

	@Test
	public void test() {
		
		String tableData = "Activity,Indictator Name,Units\n" +
						   "NFI Distribution,Number of households,households\n" +
						   "School Reconstruction,Number of classrooms,classrooms\n" +
						   "Awareness raising,Number of benes,people\n";
		
		UserDatabaseDTO database = new UserDatabaseDTO(1, "RDC");
		database.getActivities().add(new ActivityDTO(1, "NFI Distribution"));
		database.getActivities().add(new ActivityDTO(2, "School Reconstruction"));
	
		IndicatorImporter importer = new IndicatorImporter(database);
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
