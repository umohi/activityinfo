package org.activityinfo.api2.shared.beans;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * Creates a new instance
 */
public interface InstanceBeanFactory {

    public <T extends InstanceBean> T wrap(Class<T> beanClass, FormInstance instance);

    public <T extends InstanceBean> T parse(Class<T> beanClass, String json);

    public <T extends InstanceBean> T create(Class<T> beanClass, Cuid id);

}
