package org.activityinfo.geoadmin;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.activityinfo.geoadmin.model.AdminUnit;

import com.vividsolutions.jts.geom.Envelope;

public class ParentGuesser {

	private static final double MIN_SCORE = 0.75;

	private ImportSource importSource;
	private List<AdminUnit> parents;

	public enum Quality {
		OK,
		WARNING,
		SEVERE
	}


	public ParentGuesser(ImportSource importSource, List<AdminUnit> parents) {
		super();
		this.importSource = importSource;
		this.parents = parents;
	}

	public AdminUnit[] run() throws IOException {
		AdminUnit[] matches = new AdminUnit[importSource.getFeatureCount()];
		for(int i=0;i!=matches.length;++i) {
			matches[i] = findBestMatch(i);
		}
		return matches;

	}

	private AdminUnit findBestMatch(int featureIndex) {
		double bestScore = MIN_SCORE;
		AdminUnit bestParent = null;
		for(AdminUnit parent : parents) {
			double score = scoreParent(featureIndex, parent);
			if(score > bestScore) {
				bestScore = score;
				bestParent = parent;
			}
		}
		return bestParent;
	}

	private double scoreParent(int featureIndex, AdminUnit parent) {

		// parent should completely contain the child
		// find the proportion contained
		double propContained = scoreGeography(featureIndex, parent);

		// check the name similarity
		double nameSimilarity = scoreName(featureIndex, parent);

		// check for the presence of the code
		double codeScore = scoreCodeMatch(featureIndex, parent);

		System.out.println(String.format("%s <> %s %.2f %.2f %.2f",
				importSource.featureToString(featureIndex),
				parent.getName(),
				propContained, nameSimilarity, codeScore));

		return propContained + nameSimilarity + codeScore;
	}

	public double scoreName(int featureIndex, AdminUnit parent) {
		double nameSimilarity = importSource.similarity(featureIndex, parent.getName());
		return nameSimilarity;
	}

	public double scoreCodeMatch(int featureIndex, AdminUnit parent) {
		if(parent.getCode() != null) {
			if(importSource.hasCode(featureIndex, parent.getCode())) {
				return 1;
			}
		}
		return 0;
	}

	public double scoreGeography(int featureIndex, AdminUnit parent) {
		Envelope parentEnvelope = GeoUtils.toEnvelope(parent.getBounds());
		Envelope childEnvelope = importSource.getEnvelope(featureIndex);
		double propContained = parentEnvelope.intersection(childEnvelope).getArea() /
				childEnvelope.getArea();
		return propContained;
	}

	public Quality quality(int featureIndex, AdminUnit parent) {

		double geoScore = scoreGeography(featureIndex, parent);
		if(geoScore < 0.97) {
			return Quality.WARNING;
		}
		if(geoScore < 0.90) {
			return Quality.SEVERE;
		}

		double nameScore = scoreName(featureIndex, parent);
		if(nameScore < 0.80) {
			return Quality.SEVERE;
		}

		if(nameScore < 1) {
			return Quality.WARNING;
		}

		return Quality.OK;

	}
}
