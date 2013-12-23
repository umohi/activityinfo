package org.activityinfo.ui.core.client.model.schema;

public interface Country {

    /**
     * 
     * @return ActivityInfo's internal id for this country
     */
    int getId();
    
    void setId(int id);
    
    /**
     * 
     * @return the name of this country
     */
    String getName();
    
    void setName(String name);
    
    /**
     * 
     * @return the standard ISO-2 code for this country
     */
    String getCode();
    
    void setCode(String code);
    
}
