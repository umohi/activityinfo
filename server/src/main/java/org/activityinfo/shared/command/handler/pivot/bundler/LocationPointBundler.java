package org.activityinfo.shared.command.handler.pivot.bundler;

import org.activityinfo.shared.command.result.Bucket;
import org.activityinfo.shared.report.content.AiLatLng;

import com.bedatadriven.rebar.sql.client.SqlResultSetRow;

/**
 * Bundles the location's coordinates into the bucket, or if
 * the location doesn't have coordinates, it falls back to 
 * the admin entity's MBR for coordinates.
 */
public class LocationPointBundler implements Bundler {

	@Override
	public void bundle(SqlResultSetRow row, Bucket bucket) {
	    // Try first to get a point from the location
		if(!row.isNull("LX") && !row.isNull("LY")) {
			bucket.setPoint(new AiLatLng(row.getDouble("LY"), row.getDouble("LX")));
			
		// Otherwise try from the admin levels
		} else if(!row.isNull("AX") && !row.isNull("AY")) {
			bucket.setPoint(new AiLatLng(row.getDouble("AY"), row.getDouble("AX")));
			
		// if that fails, use the country's centroid
		} else if(!row.isNull("CX") && !row.isNull("CY")) {
		    bucket.setPoint(new AiLatLng(row.getDouble("CY"), row.getDouble("CX")));
		}
	}
}
