package org.activityinfo.core.shared.importing.validation;

import com.google.common.collect.Maps;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.importing.strategy.InstanceScorer;

import java.util.Map;
import java.util.Set;

public class ValidationResult {

    public static enum State {
        OK, MISSING, ERROR, CONFIDENCE
    }

    public static final ValidationResult MISSING = new ValidationResult(State.MISSING) {};

    public static final ValidationResult OK = new ValidationResult(State.OK) {};

    private final State state;
    private Map<Cuid,Set<Cuid>> rangeInstanceIds = Maps.newHashMap();
    private Cuid instanceId;
    private String typeConversionErrorMessage;
    private String convertedValue;
    private double confidence;

    private ValidationResult(State state) {
        this.state = state;
    }

    public static ValidationResult error(String message) {
        ValidationResult result = new ValidationResult(State.ERROR);
        result.typeConversionErrorMessage = message;
        return result;
    }

    public static ValidationResult converted(String value, double confidence) {
        ValidationResult result = new ValidationResult(State.CONFIDENCE);
        result.convertedValue = value;
        result.confidence = confidence;
        return result;
    }

    public boolean hasTypeConversionError() {
        return typeConversionErrorMessage != null;
    }

    public String getTypeConversionErrorMessage() {
        return typeConversionErrorMessage;
    }

    public String getConvertedValue() {
        return convertedValue;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean wasConverted() {
        return convertedValue != null;
    }

    public State getState() {
        return state;
    }

    public boolean shouldPersist() {
        return state == State.OK || (state == State.CONFIDENCE && confidence >= InstanceScorer.MINIMUM_SCORE);
    }

    public Cuid getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Cuid instanceId) {
        this.instanceId = instanceId;
    }

    public Map<Cuid, Set<Cuid>> getRangeInstanceIds() {
        return rangeInstanceIds;
    }
}
