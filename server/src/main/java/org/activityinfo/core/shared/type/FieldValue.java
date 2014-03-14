package org.activityinfo.core.shared.type;

import org.activityinfo.core.shared.serialization.SerValue;

/**
 * Required interface for all FieldValue types
 */
public interface FieldValue {

    /**
     *
     * @return the id of this values type class. Type Classes are the most general level of typing
     * that we have- they may be further refined by a
     */
    String getTypeClassId();

    /**
     *
     * @return a serialized form of this value
     */
    SerValue serialize();
}
