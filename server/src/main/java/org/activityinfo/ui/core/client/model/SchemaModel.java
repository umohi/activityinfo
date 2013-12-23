package org.activityinfo.ui.core.client.model;

import java.util.List;

import org.activityinfo.ui.core.client.model.schema.Activity;
import org.activityinfo.ui.core.client.model.schema.Country;

public interface SchemaModel extends Model {

    /**
     * 
     * @return this database's id
     */
    int getId();
    
    void setId(int id);
    
    /**
     * 
     * @return this databases' country
     */
    Country getCountry();
    
    void setCountry(Country country);
    
    /**
     * 
     * @return this database's name
     */
    String getName();
    
    void setName(String name);
    
    /**
     * 
     * @return the full description of this database
     */
    String getDescription();
    
    void setDescription(String string);
    
    /**
     * 
     * @return true if we are the owner of this database
     */
    boolean isOwned();
    
    /**
     * 
     * @return true if the authenticated user is allowed to design this database.
     */
    boolean isDesignAllowed();
    
    /**
     * 
     * @return this database's activities
     */
    List<Activity> getActivities();
    
    void setActivities(List<Activity> activities);
    
    long getLastSyncedTime();
    
    void setLastSyncedTime(long time);

    
}
