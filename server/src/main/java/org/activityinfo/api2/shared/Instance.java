package org.activityinfo.api2.shared;


public interface Instance {

    /**
     *
     * @return the universally-unique identifier for this instance
     */
    Cuid getId();

    /**
     *
     * @return the id of this instance's class
     */
    Cuid getClassId();

}
