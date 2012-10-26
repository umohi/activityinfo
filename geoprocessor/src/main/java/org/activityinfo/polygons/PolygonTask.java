package org.activityinfo.polygons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;

import org.activityinfo.GeoTask;

import com.google.common.collect.Sets;

public class PolygonTask implements GeoTask {

	private File root;
	private File adminRoot;

	private Set<Integer> adminLevelIds = Sets.newHashSet();
	
	public void run(String geodbRoot) throws Exception {
		
		root = assertExists(new File(geodbRoot));
		adminRoot = assertExists(new File(root, "admin"));
		
		processAll(adminRoot);
		writeSql();
	}

	private void writeSql() throws FileNotFoundException {
		String sqlFile = root + File.separator + "geometry" + File.separator + "update.sql";
		PrintWriter sql = new PrintWriter(sqlFile);
		sql.println("UPDATE AdminLevel SET polygons = 0;");
		for(Integer adminLevelId : adminLevelIds) {
			sql.println("UPDATE AdminLevel SET polygons = 1 WHERE adminLevelId = " + adminLevelId + ";");
		}
		sql.close();
	}

	private void processAll(File parent) throws Exception {
		for(File child : parent.listFiles()) {
			if(child.isDirectory() && !child.getName().equals("GAUL")) {
				processAll(child);
			} else if(child.getName().endsWith(".properties")) {
				PolygonBuilder builder = new PolygonBuilder(root, child);	
				builder.run();
				
				adminLevelIds.add(builder.getAdminLevelId());
			}
		}		
	}

	private File assertExists(File file) {
		if(!file.exists()) {
			throw new RuntimeException(file.getAbsoluteFile() + " does not exist");
		}
		return file;
	}
}
