package org.activityinfo.tiles;

import org.geotools.sld.SLDConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;

public class RenderTilesTask {

	
	public void parseStyle() {
		Configuration config = new SLDConfiguration();
		Parser parser = new Parser(config);

	}
}
