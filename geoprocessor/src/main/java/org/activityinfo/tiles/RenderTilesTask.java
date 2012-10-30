package org.activityinfo.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.sld.SLDConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class RenderTilesTask {
	private File root;
//
//	public void run(String geodb) throws Exception {
//		this.root = new File(geodb);
//		parseStyle();
//		
//		CoordinateReferenceSystem crs = CRS.decode("EPSG:3785");
//		MapContext map = new DefaultMapContext(crs);
//	
//		
//		GTRenderer renderer = new StreamingRenderer();
//	    renderer.setContext(map);
//		
//	    Rectangle imageBounds = null;
//	    ReferencedEnvelope mapBounds = null;
//	    try {
//	        mapBounds = map.getMaxBounds();
//	        double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
//	        imageBounds = new Rectangle(
//	                0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));
//	 
//	    } catch (Exception e) {
//	        // failed to access map layers
//	        throw new RuntimeException(e);
//	    }
//	 
//	    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);
//	 
//	    Graphics2D gr = image.createGraphics();
//	    gr.setPaint(Color.WHITE);
//	    gr.fill(imageBounds);
//	 
//	    try {
//	        renderer.paint(gr, imageBounds, mapBounds);
//	        File fileToSave = new File(file);
//	        ImageIO.write(image, "jpeg", fileToSave);
//	 
//	    } catch (IOException e) {
//	        throw new RuntimeException(e);
//	    }
//		
//	}

	public void parseStyle() {
		Configuration config = new SLDConfiguration();
		Parser parser = new Parser(config);
	//	FileInputStream in = new FileInputStream(root.getAbsolutePath() + "style/"
		//parser.parse()
	}
}
