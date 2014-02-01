package org.activityinfo.api2.shared;

import java.io.Serializable;

/**
 * Internationalized Resource Identifier
 */
public class Iri {

    private final String string;

    public Iri(String string) {
        this.string = string;
    }

    /**
     * @return an encoded string representation of the IRI
     */
    public String asString() {
        return string;
    }

    @Override
    public String toString() {
        return "<" + string + ">";
    }

    /**
     *
     * @return the IRI's scheme (for example, http, cuid, etc)
     */
    public String getScheme() {
        return string.substring(0, string.indexOf(':'));

    }

    /**
     *
     * @return the scheme-specific part of the IRI
     */
    public String getSchemeSpecificPart() {
        return string.substring(string.indexOf(':')+1);
    }

}
