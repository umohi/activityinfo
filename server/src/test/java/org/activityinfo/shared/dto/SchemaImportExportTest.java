package org.activityinfo.shared.dto;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.activityinfo.client.importer.data.PastedImportSource;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.io.Resources;

public class SchemaImportExportTest {

	private PrintWriter out;
	
	@Test
	public void parseComma() {
		String data = 
				"Id,ActivityCategory,ActivityName,FormFieldType,Category,Name,multipleAllowed,mandatory,Description,Units,AttributeId,AttributeValue\n" +
				",CASH-1. Need Coverage,2. Provision of regular cash assistance,AttributeGroup,,"
				+ "Location type,1,1,,,4624,\"Public Infrastructure (School, Health center,etc.)\"\n";
		
		PastedImportSource source = new PastedImportSource(data);
		assertThat(source.get(0, 1), equalTo("CASH-1. Need Coverage"));
		assertThat(source.get(0, 11), equalTo("Public Infrastructure (School, Health center,etc.)"));
	}
	
	@Test
	public void reformat() throws IOException {

		out = new PrintWriter("/home/alex/test.csv");
		
		
		String csv = Resources.toString(Resources.getResource("schema_1064.csv"), Charsets.UTF_8);
		PastedImportSource source = new PastedImportSource(csv);
		
		int row =0;
		int numRows = source.getRows().size();
		while(row < numRows) {
			String activityCategory = source.get(row, 1);
			String activityName = source.get(row, 2);
			
			String _ = "";

			row(_, "Activity", activityName);
			row(_, _, "Location Type", "Village");
			row(_, _, "Reporting Frequency", "Village");
			
			row();
			row(_, "Category", "Field Type", "Name", "Description", "Units");
			
			while(row < numRows &&
				  Objects.equal(activityCategory, source.get(row, 1)) &&
				  Objects.equal(activityName, source.get(row, 2))) {
				
				String type = source.get(row, 3);
				if(type.equals("AttributeGroup")) {
					String groupName = source.get(row, 5);
					row(_, _, "AttributeGroup", groupName);
					
					while(groupName.equals(source.get(row, 5))) {
						//row(_, source.get(row, 11));
						row++;
					}
				} else if(type.equals("Indicator")) {
					row(_, source.get(row, 4), "Indicator", source.get(row, 5), source.get(row, 8), source.get(row, 9));
					row++;
				}
			}
			row();
			row();
		}
		out.close();
	}

	private void row(String... columns) {
		boolean needsTab = false;
		for(String col : columns) {
			if(needsTab) {
				out.print("\t");
			}
			out.print(col);
			needsTab = true;
		}
		out.println();
	}
	
}
