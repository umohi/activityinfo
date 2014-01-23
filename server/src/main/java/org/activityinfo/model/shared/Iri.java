package org.activityinfo.model.shared;

/**
 * Internationalized Resource Identifier
 *
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
}
