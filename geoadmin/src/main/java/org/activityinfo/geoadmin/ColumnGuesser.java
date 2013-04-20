package org.activityinfo.geoadmin;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Finds a column within an import source that matches certain criteria. For
 * example, if we want to preselect the column containing the entity name, we
 * will look for a column containing mostly names [A-Za-z ]+ with few
 * duplicates.
 */
public class ColumnGuesser {
    private Pattern regex;

    /**
     * Creates a guesser for a
     * 
     * @param pattern
     * @return
     */
    public ColumnGuesser forPattern(String pattern) {
        this.regex = Pattern.compile(pattern);
        return this;
    }

    public ColumnGuesser favoringUniqueValues() {
        return this;
    }

    /**
     * Finds the index of the column/attribute in the import source that best
     * matches the given criteria
     */
    public int findBest(ImportSource source) {
        int bestAttribute = -1;
        double bestScore = 0;

        for (int attributeIndex = 0; attributeIndex != source.getAttributeCount(); ++attributeIndex) {
            double score = scoreColumn(source, attributeIndex);
            if (score > bestScore) {
                bestScore = score;
                bestAttribute = attributeIndex;
            }
        }
        return bestAttribute;
    }

    /**
     * Scores a column based on the provided criteria. 0=poor match, high=good
     * match
     */
    private double scoreColumn(ImportSource source, int attributeIndex) {
        double score = 0;

        score += scorePattern(source, attributeIndex);
        score += scoreUnique(source, attributeIndex);

        return score;
    }

    /**
     * Scores a given column/attribute based on the provided regex.
     * 
     * @param source
     *            the import source
     * @param attributeIndex
     *            the index of the attribute in the ImportScore to evaluate
     * @return the proportion of values in the column that match the regex.
     *         (0=poor match,1=perfect match)
     */
    private double scorePattern(ImportSource source, int attributeIndex) {
        int numMatching = 0;

        for (ImportFeature feature : source.getFeatures()) {
            String value = feature.getAttributeStringValue(attributeIndex);
            if (regex.matcher(value).matches()) {
                numMatching++;
            }
        }
        return ratio(numMatching, source.getFeatures().size());
    }

    /**
     * Scores the column on the uniqueness of its values, from 1=all values are
     * unique.
     */
    private double scoreUnique(ImportSource source, int attributeIndex) {
        Set<String> values = Sets.newHashSet();

        for (ImportFeature feature : source.getFeatures()) {
            String value = feature.getAttributeStringValue(attributeIndex);
            if (!Strings.isNullOrEmpty(value)) {
                values.add(value);
            }
        }

        return ratio(values.size(), source.getFeatureCount());
    }

    private double ratio(double numerator, double denominator) {
        return numerator / denominator;
    }
}
