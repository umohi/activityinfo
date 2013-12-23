package org.activityinfo.ui.core.client.model.schema;

import java.util.List;

public interface Activity {

    int getId();
    
    void setId();
    
    String getName();
    
    void setName(String name);
    
    int getReportingFrequency();
    
    void setReportingFrequency();
    
    List<Indicator> getIndicators();
    
    void setIndicators(List<Indicator> indicators);
    
}
