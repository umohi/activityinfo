package org.activityinfo.geoadmin;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for matching place names.
 */
public class PlaceNames {

    /**
     * Calculates the similarity between two names based on Levenshtein edit
     * distance on a scale of 0 to 1.
     * 
     * @return a score between 0=no similarity, 1=exact match
     */
    public static double similiarity(String a, String b) {
        String cleanA = cleanName(a);
        String cleanB = cleanName(b);
        double distance = StringUtils.getLevenshteinDistance(cleanA, cleanB);
        double maxDistance = Math.max(cleanA.length(), cleanB.length());
        return (maxDistance - distance) / maxDistance;
    }

    /**
     * Calculates the edit distance between two names.
     * 
     * @param a
     * @param b
     * @return
     */
    public static int distance(String a, String b) {
        return StringUtils.getLevenshteinDistance(PlaceNames.cleanName(a), PlaceNames.cleanName(b));
    }

    /**
     * Cleans up a name by removing all punctuation and non-letters.
     */
    public static String cleanName(String name) {
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i != name.length(); ++i) {
            int cp = name.codePointAt(i);
            if (Character.isLetter(cp)) {
                cleaned.appendCodePoint(Character.toLowerCase(cp));
            }
        }
        return cleaned.toString();
    }
}
