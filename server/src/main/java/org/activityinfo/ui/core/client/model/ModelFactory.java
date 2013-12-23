package org.activityinfo.ui.core.client.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * Generated factory class for creating Models using Autobeans.
 * 
 * See https://code.google.com/p/google-web-toolkit/wiki/AutoBean for more 
 * information about GWT AutoBeans
 */
public interface ModelFactory extends AutoBeanFactory {

    AutoBean<DatabaseItemList> databaseItemList();
    
    AutoBean<SchemaModel> schemaResource();
}
