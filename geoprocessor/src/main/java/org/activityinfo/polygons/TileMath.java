package org.activityinfo.polygons;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;


/**
 * Provides a series of utility functions useful for processing
 * map tiles created in the Googlian sense (spherical mercator)
 * on the server.
 * 
 * See:
 * <ul>
 * <li>http://en.wikipedia.org/wiki/Mercator_projection</li>
 * <li>http://code.google.com/apis/maps/documentation/overlays.html#CustomMapTiles</li>
 * </ul>
 * 
 * @author Alex Bertram
 *
 */
public final class TileMath {

	private static final int MAX_ZOOM = 16;
	/*
	 * Various math constants
	 */
	private static final double D2R = 0.0174532925;
	private static final double EPSLN = 1.0e-10;
	private static final double HALF_PI = 1.5707963267948966192313216916398;
	private static final double TWO_PI = 6.283185307179586476925286766559;
	private static final double FORTPI = 0.78539816339744833;
	private static final double DEGREES_PER_RADIAN = 57.2957795;

	private static final int TILE_SIZE = 256;
		
	private TileMath() {}
	
	/**
	 * Returns the circumference of the projected coordinate 
	 * system at the equator (and elsewhere perhaps? )
	 * 
	 * @param zoom
	 * @return
	 */
	public static int size(int zoom) {
		return (int)(float)(Math.pow(2, zoom) * TILE_SIZE);
	}
	
	public static double radius(int zoom) { 
		return size(zoom) / TWO_PI;
	}

	/**
	 * 
	 * Projects a geographic X (longitude) coordinate forward into
	 * the Googlian screen pixel coordinate system, based on a given
	 * zoom level
	 * 
	 * @param lng
	 * @param zoom
	 * @return
	 */
	public static Coordinate fromLatLngToPixel(Coordinate coordinate, int zoom) {
		double size = size(zoom);
		double radius = size / TWO_PI;
		
		double x = (coordinate.x + 180.0) / 360.0 * size; 
			
		double lat = coordinate.y * D2R;
		
		if(Math.abs( Math.abs(lat) - HALF_PI)  <= EPSLN) {
			throw new IllegalArgumentException("Too close to the poles to project");
		}
		
		double ty = (size) / 2.0;
	    double y = radius * Math.log(Math.tan(FORTPI + 0.5*lat));
	    	   y = ty - y;
			   
		return new Coordinate( x, y );
	}
	
	public static Coordinate inverse(Coordinate px, int zoom) {

		double size = size(zoom);
		double radius = size / TWO_PI;

		double lng = (px.x / size * 360d) - 180d;
		
		double ty = (size)/2.0; 
		double y = (ty - px.y) / radius;
		double lat = Math.atan(Math.sinh(y)) * DEGREES_PER_RADIAN;
		
		return new Coordinate(lng, lat);
	}

	/**
	 * 
	 * Returns the maximum zoom level at which the given extents will fit inside
	 * the map of the given size
	 * 
	 * @param extent
	 * @param mapWidth
	 * @param mapHeight
	 * @return
	 */
	public static int zoomLevelForExtents(Envelope extent, int mapWidth, int mapHeight) {

		int zoomLevel = 1;

		do {

			Coordinate upperLeft = fromLatLngToPixel(new Coordinate(extent.getMinX(), extent.getMaxY()), zoomLevel);
			Coordinate lowerRight = fromLatLngToPixel(new Coordinate(extent.getMaxY(), extent.getMinY()), zoomLevel);
			
			double extentWidth = lowerRight.x - upperLeft.y;
			
			//assert extentWidth >= 0;

			if(extentWidth > mapWidth) {
                return zoomLevel - 1;
            }

			double extentHeight = lowerRight.y - upperLeft.y;
			
			if(extentHeight > mapHeight) {
                return zoomLevel - 1;
			}

			zoomLevel++;
			
		} while(zoomLevel < MAX_ZOOM);

		return zoomLevel;
	}
	

//	/**
//	 * Returns the coordinate of the tile given a point in the
//	 * projected coordinate system.
//	 * 
//	 * @param px
//	 * @return
//	 */
//	public static Tile tileForPoint(Point px) {	
//		return new Tile( px.getX() / TILE_SIZE, px.getY() / TILE_SIZE );
//	}
}