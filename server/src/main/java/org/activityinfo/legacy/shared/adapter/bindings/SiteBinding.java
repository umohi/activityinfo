package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormInstance;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;

/**
 * Defines a two-way binding between Sites and FormInstances
 */
public class SiteBinding extends ModelBinding<SiteDTO> {

    private final ActivityDTO activity;

    SiteBinding(ActivityDTO activity) {
        super(CuidAdapter.activityFormClass(activity.getId()), CuidAdapter.SITE_DOMAIN);
        this.activity = activity;
    }

    public ActivityDTO getActivity() {
        return activity;
    }

    public Cuid getLocationField() {
        return locationField(activity.getId());
    }

    public int getAdminEntityId(FormInstance instance) {
        return getLegacyIdFromCuid(instance.getInstanceId(getLocationField()));
    }

    public LocationTypeDTO getLocationType() {
        return activity.getLocationType();
    }
}
