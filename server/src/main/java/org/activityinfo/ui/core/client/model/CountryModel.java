package org.activityinfo.ui.core.client.model;

/**
 * Models a geographic country. 
 */
public class CountryModel {
    private int id;
    private String name;
    private String code;
    
    /**
     * 
     * @return ActivityInfo's internal id for the country
     */
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the country's name
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the country's two-letter ISO code.
     */
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
