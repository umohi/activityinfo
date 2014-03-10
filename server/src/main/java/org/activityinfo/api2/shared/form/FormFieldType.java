package org.activityinfo.api2.shared.form;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * The type of field, which influences how input is presented
 * the user, how it is validated, and what default measures
 * are available.
 */
public enum FormFieldType {

    /**
     * Numeric quantity, expressed in certain units
     */
    QUANTITY {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(NARRATIVE, FREE_TEXT);
        }
    },

    /**
     * A longish block of text
     */
    NARRATIVE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, QUANTITY, LOCAL_DATE);
        }
    },

    /**
     * Short free text field
     */
    FREE_TEXT {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(QUANTITY, LOCAL_DATE, NARRATIVE);
        }
    },

    /**
     * A Gregorian calendar date, with no time zone attached
     */
    LOCAL_DATE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, NARRATIVE);
        }
    },

    /**
     * A geographic point, expressed as latitude / longitude
     */
    GEOGRAPHIC_POINT {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet(FREE_TEXT, NARRATIVE);
        }
    },

    /**
     * References another FormInstance or RDFS resource
     */
    REFERENCE {
        @Override
        public Set<FormFieldType> getAllowedConvertTo() {
            return Sets.newHashSet();
        }
    };

    FormFieldType() {
    }

    public abstract Set<FormFieldType> getAllowedConvertTo();
}
