package org.activityinfo.ui.full.client.importer.draft;

import org.activityinfo.ui.full.client.importer.match.ValueStatus;

/**
 * Intermediate structure for a Field value that has been matched/parsed but
 * may still require input or validation from user.
 */
public class DraftFieldValue {
    private Object matchedValue;
    private Object importedValue;
    private boolean conversionError;

    private double matchScore;

    /**
     *
     * @return the value imported by the user
     */
    public Object getImportedValue() {
        return importedValue;
    }

    /**
     *
     * @return the matched/converted value
     */
    public Object getMatchedValue() {
        return matchedValue;
    }


    public void setMatchedValue(Object matchedValue) {
        this.matchedValue = matchedValue;
    }

    public void setImportedValue(Object importedValue) {
        this.importedValue = importedValue;
    }

    /**
     *
     * @return true if there was an error during type conversion from the imported value
     * to the matched value
     */
    public boolean isConversionError() {
        return conversionError;
    }

    public void setConversionError(boolean conversionError) {
        this.conversionError = conversionError;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    /**
     *
     * @return for fields used in reference matching, a score showing the similarity between the imported value
     * and the corresponding value in the referenced instance
     */
    public double getMatchScore() {
        return matchScore;
    }
}
