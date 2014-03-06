package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.command.GetSchema;
import org.activityinfo.api2.client.NotFoundException;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormClass;

import static org.activityinfo.api.shared.adapter.CuidAdapter.*;

public class ClassProvider {
    private final Dispatcher dispatcher;

    public ClassProvider(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Promise<FormClass> get(Cuid classId) {
        switch (classId.getDomain()) {
            case ACTIVITY_DOMAIN:
                int activityId = getLegacyIdFromCuid(classId);
                return dispatcher.execute(new GetSchema()).then(new BuiltinFormClasses.ActivityAdapter(activityId));

            case PARTNER_FORM_CLASS_DOMAIN:
                return Promise.resolved(PartnerClassAdapter.create(getLegacyIdFromCuid(classId)));

            case PROJECT_CLASS_DOMAIN:
                return Promise.resolved(BuiltinFormClasses.projectFormClass(getLegacyIdFromCuid(classId)));

            case ATTRIBUTE_GROUP_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new AttributeClassAdapter(getLegacyIdFromCuid(classId)));

            case ADMIN_LEVEL_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new AdminLevelClassAdapter(getLegacyIdFromCuid(classId)));

            case LOCATION_TYPE_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new LocationClassAdapter(getLegacyIdFromCuid(classId)));

            default:
                return Promise.rejected(new NotFoundException(classId.asIri()));
        }
    }

}
