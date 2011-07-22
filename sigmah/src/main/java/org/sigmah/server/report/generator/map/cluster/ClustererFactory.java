package org.sigmah.server.report.generator.map.cluster;

import java.util.List;

import org.sigmah.server.report.generator.map.RadiiCalculator;
import org.sigmah.server.report.generator.map.cluster.auto.MarkerGraph;
import org.sigmah.server.report.generator.map.cluster.auto.MarkerGraph.IntersectionCalculator;
import org.sigmah.shared.report.model.PointValue;
import org.sigmah.shared.report.model.clustering.AutomaticClustering;
import org.sigmah.shared.report.model.clustering.Clustering;
import org.sigmah.shared.report.model.clustering.NoClustering;

public class ClustererFactory {
    public static Clusterer fromClustering(Clustering clustering, RadiiCalculator radiiCalculator, List<PointValue> points, IntersectionCalculator intersectionCalculator) {
    	if (clustering instanceof NoClustering) {
    		return new NullClusterer(radiiCalculator, points);
    	}
    	if (clustering instanceof AutomaticClustering) {
    		return new GeneticClusterer(radiiCalculator, intersectionCalculator, points);
    	}
    	
    	return new NullClusterer(radiiCalculator, points); 
    }
}
