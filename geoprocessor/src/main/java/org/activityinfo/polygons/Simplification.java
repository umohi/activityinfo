package org.activityinfo.polygons;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class Simplification {

	private static final int MAX_ZOOM = 12;
	private static final int MIN_PX = 2;
	
	public static double getMinDistance(Geometry geometry) {
		Coordinate c = geometry.getCentroid().getCoordinate();
		Coordinate p = TileMath.fromLatLngToPixel(c, MAX_ZOOM);
		Coordinate dc = TileMath.inverse(new Coordinate(p.x + MIN_PX, p.y + MIN_PX), MAX_ZOOM);
		
		return Math.sqrt( (c.x - dc.x)*(c.x - dc.x) + (c.y - dc.y)*(c.y - dc.y) );
		
	}

}
