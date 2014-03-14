package org.activityinfo.ui.client.util;

/**
 * An alternative implementation of FeatureSwitched used during
 * release mode to compile out beta features.
 */
public class DisabledFeatureSwitch {
    public static boolean isEnabled(String featureName) {
        return false;
    }
}
