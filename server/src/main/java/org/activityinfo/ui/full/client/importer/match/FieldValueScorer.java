package org.activityinfo.ui.full.client.importer.match;

/**
 * Scores the similarity between an imported value and
 * a referenced value
 */
public interface FieldValueScorer<T> {

    public double score(T importedValue, T referencedValue);
}
