package org.activityinfo.ui.core.client.model;

/**
 * Models a Partner organization or team participating in the
 * database.
 */
public class PartnerModel {
    private Long id;
    private String name;
    private String fullName;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
   
}
