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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizedString that = (LocalizedString) o;

        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value + "@" + locale;
    }
}
