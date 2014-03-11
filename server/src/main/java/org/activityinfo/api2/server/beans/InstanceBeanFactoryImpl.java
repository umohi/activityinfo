package org.activityinfo.api2.server.beans;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.beans.InstanceBean;
import org.activityinfo.api2.shared.beans.InstanceBeanFactory;
import org.activityinfo.api2.shared.form.FormInstance;

/**
 * Implementation of {@link org.activityinfo.api2.shared.beans.InstanceBeanFactory} that uses
 *  reflection.
 */
public class InstanceBeanFactoryImpl implements InstanceBeanFactory {

    @Override
    public <T extends InstanceBean> T wrap(Class<T> beanClass, FormInstance instance) {


    }

    @Override
    public <T extends InstanceBean> T parse(Class<T> beanClass, String json) {
        return null;
    }

    @Override
    public <T extends InstanceBean> T create(Class<T> beanClass, Cuid id) {
        return null;
    }
}
