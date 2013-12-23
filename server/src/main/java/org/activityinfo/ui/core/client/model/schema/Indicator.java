package org.activityinfo.ui.core.client.model.schema;

public interface Indicator {

    int getId();
    
    void setId();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription();
    
    boolean getMandatory();
    
    void setMandatory(boolean mandatory);
    
    String getUnits();
    
    void setUnit(String units);
}
