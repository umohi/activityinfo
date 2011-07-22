package org.sigmah.shared.report.model.clustering;

import javax.xml.bind.annotation.XmlRootElement;


/*
 * Does not do any clustering on map markers. Instead it just maps a pointvalue to a cluster.
 */
@XmlRootElement
public class NoClustering implements Clustering {
	@Override
	public boolean isClustered() {
		return false;
	}

}