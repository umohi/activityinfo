package org.activityinfo.model;


public class LocalizedString {

    private final String value;
    private final String locale;

    public LocalizedString(String value, String locale) {
        this.value = value;
        this.locale = locale;
    }

    public LocalizedString(String value) {
        this.value = value;
        this.locale = "en";
    }

    public String getValue() {
        return value;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return value + "@" + locale;
    }
}
