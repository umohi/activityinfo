package org.activityinfo.api2.server.beans;

import org.activityinfo.api2.shared.beans.InstanceBean;

import java.lang.reflect.Method;

/**
 * Created by alex on 3/10/14.
 */
public class InstanceBeanModel {

    private class FieldModel {



    }


    public InstanceBeanModel(Class beanClass) {
        for(Method method : beanClass.getMethods()) {

        }
    }
}
