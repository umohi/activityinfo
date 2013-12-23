package org.activityinfo.ui.desktop.client.widget.list;

import org.activityinfo.ui.core.client.model.ModelList;
import org.activityinfo.ui.core.client.resources.Resource;

public class ListResourcePanel<T> extends ResourcePanel<ModelList<T>> {

    private ListResourcePanel(Resource<ModelList<T>> index, HasResource<ModelList<T>> resourceWidget) {
        super(index, resourceWidget);
    }
    
    public static <T> ListResourcePanel<T> create(Resource<ModelList<T>> provider, 
        HasResource<ModelList<T>> widget) {
        
        return new ListResourcePanel<T>(provider, widget);   
    }
}
