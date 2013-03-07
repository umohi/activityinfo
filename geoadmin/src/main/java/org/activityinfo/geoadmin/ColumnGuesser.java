package org.activityinfo.geoadmin;

import java.util.Set;
import java.util.regex.Pattern;

import org.opengis.feature.type.PropertyDescriptor;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class ColumnGuesser {
	private Pattern regex;
	
	
	public ColumnGuesser forPattern(String pattern) {
		this.regex = Pattern.compile(pattern);
		return this;
	}
	
	public ColumnGuesser favoringUniqueValues() {
		return this;
	}
	
	public int findBest(ImportSource source) {
		int bestAttribute = -1;
		double bestScore = 0;
		
		for(int attributeIndex=0;attributeIndex!=source.getAttributeCount();++attributeIndex) {
			double score = scoreAttribute(source, attributeIndex);
			if(score > bestScore) {
				bestScore = score;
				bestAttribute = attributeIndex;
			}
		}
		return bestAttribute;
	}

	private double scoreAttribute(ImportSource source, int attributeIndex) {
		double score = 0;
		
		score += scorePattern(source, attributeIndex);
		score += scoreUnique(source, attributeIndex);
		
		return score;
	}

	private double scorePattern(ImportSource source, int attributeIndex) {
		int numMatching = 0;
		
		for(int featureIndex=0;featureIndex!=source.getFeatureCount();++featureIndex) {
			String value = source.getAttributeStringValue(featureIndex, attributeIndex);
			if(regex.matcher(value).matches()) {
				numMatching ++;
			}
		}
		return ratio(numMatching, source.getFeatureCount());
	}
	
	private double scoreUnique(ImportSource source, int attributeIndex) {
		Set<String> values = Sets.newHashSet();
		
		for(int featureIndex=0;featureIndex!=source.getFeatureCount();++featureIndex) {
			String value = source.getAttributeStringValue(featureIndex, attributeIndex);
			if(!Strings.isNullOrEmpty(value)) {
				values.add(value);
			}
		}
		
		return ratio(values.size(), source.getFeatureCount());
	}
	
	private double ratio(double numerator, double denominator) {
		return numerator / denominator;
	}
}
