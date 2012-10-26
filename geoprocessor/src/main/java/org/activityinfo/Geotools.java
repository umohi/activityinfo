package org.activityinfo;

import java.util.Map;

import org.activityinfo.polygons.GaulPolygonTask;
import org.activityinfo.polygons.PolygonTask;

import com.google.common.collect.Maps;

public class Geotools {

	public static void main(String[] args) throws Exception {
		
		Map<String, GeoTask> tasks = Maps.newHashMap();
		tasks.put("polygons", new PolygonTask());
		tasks.put("gaul-polygons", new GaulPolygonTask());
		
		if(args.length > 1 && tasks.containsKey(args[0])) {
			tasks.get(args[0]).run(args[1]);
		} else {
			System.err.println("Usage: geotools <command> [options...]");
			System.err.println();
			System.err.println("Available commands:");
			for(String taskName : tasks.keySet()) {
				System.err.println("\t" + taskName + " <geodb root>");
			}
			System.exit(-1);
		}
	}

	
}
