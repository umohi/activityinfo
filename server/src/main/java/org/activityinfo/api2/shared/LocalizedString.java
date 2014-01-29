package org.activityinfo.api2.shared;


import javax.validation.constraints.NotNull;

public class LocalizedString {

    public static final LocalizedString EMPTY = new LocalizedString("");
    public static final String DEFAULT_LOCALE = "en";

    private final String value;
    private final String locale;

    public LocalizedString(String value, String locale) {
        this.value = value != null ? value : "";
        this.locale = locale != null ? locale : DEFAULT_LOCALE;
    }

    public LocalizedString(String value) {
        this(value, DEFAULT_LOCALE);
    }

    @NotNull
    public String getValue() {
        return value;
    }

    @NotNull
    public String getLocale() {
        return locale;
    }

    public static LocalizedString nullToEmpty(LocalizedString str) {
        return str != null ? str : EMPTY;
    }

    @Override
    public String toString() {
        return value + "@" + locale;
    }
}
