package org.activityinfo.core.shared.importing.validation;

public class ValidationResult {

    public static final ValidationResult MISSING = new ValidationResult() {};

    public static final ValidationResult OK = new ValidationResult() {};

    private String typeConversionErrorMessage;
    private String convertedValue;
    private double confidence;

    private ValidationResult() {
    }

    public static ValidationResult error(String message) {
        ValidationResult result = new ValidationResult();
        result.typeConversionErrorMessage = message;
        return result;
    }

    public static ValidationResult converted(String value, double confidence) {
        ValidationResult result = new ValidationResult();
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
}
