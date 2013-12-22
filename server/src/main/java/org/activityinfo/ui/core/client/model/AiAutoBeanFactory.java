package org.activityinfo.ui.core.client.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AiAutoBeanFactory extends AutoBeanFactory {

    AutoBean<DatabaseItemList> databaseItemList();
}
