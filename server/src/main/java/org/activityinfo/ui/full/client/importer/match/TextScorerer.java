package org.activityinfo.ui.full.client.importer.match;

/**
 * Scores the similarity between an imported TEXT value and
 * the TEXT value of a referenced field
 */
public class TextScorerer implements FieldValueScorer<String> {

    private final JaroWinklerDistance distanceFunction;

    public TextScorerer() {
        this.distanceFunction = JaroWinklerDistance.DEFAULT;
    }

    @Override
    public double score(String importedValue, String referencedValue) {
        return distanceFunction.getDistance(importedValue, referencedValue);
    }
}
