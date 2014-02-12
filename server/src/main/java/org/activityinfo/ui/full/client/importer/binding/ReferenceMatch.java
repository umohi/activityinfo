package org.activityinfo.ui.full.client.importer.binding;

import org.activityinfo.api2.shared.form.FormInstance;

import java.util.Arrays;
import java.util.List;

/**
 * A potential match for a ReferenceField
 */
public class ReferenceMatch {
    private final FormInstance instance;
    private double[] scores;

    public ReferenceMatch(FormInstance instance, double[] scores) {
        this.instance = instance;
        this.scores = scores;
    }

    public Object getLabel() {
        for(Object value : instance.getValueMap().values()) {
            if(value instanceof String) {
                return value;
            }
        }
        return "<empty>";
    }

    public double[] getScores() {
        return scores;
    }

    public double getScore(int i) {
        return scores[i];
    }

    public FormInstance getInstance() {
        return instance;
    }

    public String prettyPrintScores() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i!=scores.length;++i) {
            if(i > 0) {
                sb.append(" ");
            }
            String score = "   " + ((int)(scores[i]*100d));
            sb.append(score.substring(score.length()-3));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "[" + getLabel() + "]" + prettyPrintScores();
    }


}
