package org.activityinfo;

import org.activityinfo.polygons.PolygonTask;

public class Geotools {

	public static void main(String[] args) throws Exception {
		if(args.length > 1 && args[0].equals("polygons")) {
			new PolygonTask().run(args[1]);
		} else {
			System.err.println("Usage: geotools <command> [options...]");
			System.err.println();
			System.err.println("Available commands:");
			System.err.println("\tpolygons <geodb root>");
			System.err.println("\tbbox shapefile");
			System.exit(-1);
		}
	}

	
}
