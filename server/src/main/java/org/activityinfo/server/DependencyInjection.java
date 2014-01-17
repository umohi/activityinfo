package org.activityinfo.server;

import com.google.inject.Injector;

/**
 * Utility class to support objects that are created outside the normal
 * path -- for example map reduce jobs
 */
public class DependencyInjection {

    public static Injector INJECTOR;
}
