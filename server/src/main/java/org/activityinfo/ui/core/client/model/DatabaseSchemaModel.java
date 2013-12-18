package org.activityinfo.ui.core.client.model;

import java.util.List;

/**
 * Models the structure of a user's database.
 */
public class DatabaseSchemaModel {
    private int id;
    private String name;
    private CountryModel country;
    private List<PartnerModel> partners;
    private List<ActivityModel> activities;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public CountryModel getCountry() {
        return country;
    }
    public void setCountry(CountryModel country) {
        this.country = country;
    }
    public List<PartnerModel> getPartners() {
        return partners;
    }
    public void setPartners(List<PartnerModel> partners) {
        this.partners = partners;
    }
    public List<ActivityModel> getActivities() {
        return activities;
    }
    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
    }
    
}
