package org.activityinfo.core.shared.importing.strategy;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.importing.match.names.LatinPlaceNameScorer;
import org.activityinfo.core.shared.importing.source.SourceRow;

import java.util.List;

/**
 * @author yuriyz on 5/19/14.
 */
public class InstanceScorer {

    public static class Score {
        private final double bestScore;
        private final double[] bestScores;
        private final int bestMatchIndex;
        private final String[] imported;

        public Score(double bestScore, double[] bestScores, int bestMatchIndex, String[] imported) {
            this.bestScore = bestScore;
            this.bestScores = bestScores;
            this.bestMatchIndex = bestMatchIndex;
            this.imported = imported;
        }

        public double getBestScore() {
            return bestScore;
        }

        public double[] getBestScores() {
            return bestScores;
        }

        public int getBestMatchIndex() {
            return bestMatchIndex;
        }

        public String[] getImported() {
            return imported;
        }
    }

    public static final double MINIMUM_SCORE = 0.5;

    private final List<ColumnAccessor> sources;
    private final List<Cuid> referenceInstanceIds;
    private final List<String[]> referenceValues;
    private final LatinPlaceNameScorer scorer = new LatinPlaceNameScorer();

    public InstanceScorer(List<String[]> referenceValues, List<Cuid> referenceInstanceIds, List<ColumnAccessor> sources) {
        this.referenceValues = referenceValues;
        this.referenceInstanceIds = referenceInstanceIds;
        this.sources = sources;
    }

    public Score score(SourceRow row) {
        double bestScore = 0;
        double bestScores[] = new double[sources.size()];
        int bestMatchIndex = -1;

        String[] imported = toArray(row);

        if(imported != null) {
            for(int i=0;i!=referenceInstanceIds.size();++i) {
                double[] score = scorePotentialMatch(imported, referenceValues.get(i));
                if(score != null) {
                    double total = sum(score);
                    if(total > bestScore) {
                        bestMatchIndex = i;
                        bestScore = total;
                        bestScores = score;
                    }
                }
            }
        }
        return new Score(bestScore, bestScores, bestMatchIndex, imported);
    }

    private double sum(double[] score) {
        double sum = 0;
        for(int i=0;i!=score.length;++i) {
            sum += score[i];
        }
        return sum;
    }

    private double[] scorePotentialMatch(String[] imported, String[] reference) {
        double scores[] = new double[imported.length];
        double max = 0;
        for(int i=0;i!=imported.length;++i) {
            if(imported[i] != null && reference[i] != null) {
                scores[i] = scorer.score(imported[i], reference[i]);
                max = Math.max(scores[i], max);
            }
        }
        if(max > MINIMUM_SCORE) {
            return scores;
        } else {
            return null;
        }
    }

    private String[] toArray(SourceRow row) {
        String[] values = new String[sources.size()];
        for(int i=0;i!=sources.size();++i) {
            if(!sources.get(i).isMissing(row)) {
                values[i] = sources.get(i).getValue(row);
            }
        }
        return values;
    }

}
