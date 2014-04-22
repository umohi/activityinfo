package org.activityinfo.core.shared.importing.validation;

public class ValidationResult {

    public static enum State {
        OK, MISSING, ERROR
    }

    public static final ValidationResult MISSING = new ValidationResult(State.MISSING) {};

    public static final ValidationResult OK = new ValidationResult(State.OK) {};

    private final State state;
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
        ValidationResult result = new ValidationResult(State.OK);
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
}
