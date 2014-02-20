package org.activityinfo.api.shared.adapter;

import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.command.GetSchema;
import org.activityinfo.api.shared.command.GetSites;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.NotFoundException;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.Resource;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import java.util.List;

import static org.activityinfo.api.shared.adapter.BuiltinFormClasses.ActivityAdapter;
import static org.activityinfo.api.shared.adapter.CuidAdapter.*;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final Dispatcher dispatcher;

    public ResourceLocatorAdaptor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Promise<FormClass> getFormClass(Cuid formId) {
        switch (formId.getDomain()) {
            case ACTIVITY_DOMAIN:
                int activityId = getLegacyIdFromCuid(formId);
                return dispatcher.execute(new GetSchema()).then(new ActivityAdapter(activityId));

            case PARTNER_FORM_CLASS_DOMAIN:
                return Promise.resolved(PartnerClassAdapter.create(getLegacyIdFromCuid(formId)));

            case PROJECT_CLASS_DOMAIN:
                return Promise.resolved(BuiltinFormClasses.projectFormClass(getLegacyIdFromCuid(formId)));

            case ATTRIBUTE_GROUP_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new AttributeClassAdapter(getLegacyIdFromCuid(formId)));

            case ADMIN_LEVEL_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new AdminLevelClassAdapter(getLegacyIdFromCuid(formId)));

            case LOCATION_TYPE_DOMAIN:
                return dispatcher.execute(new GetSchema()).then(new LocationClassAdapter(getLegacyIdFromCuid(formId)));

            default:
                return Promise.rejected(new NotFoundException(formId.asIri()));
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid instanceId) {
        if (instanceId.getDomain() == SITE_DOMAIN) {
            final int siteId = getLegacyIdFromCuid(instanceId);

            Promise<SchemaDTO> schema = dispatcher
                    .execute(new GetSchema());
            Promise<SiteDTO> sites = dispatcher
                    .execute(GetSites.byId(siteId))
                    .then(new SingleListResultAdapter<SiteDTO>());
            return Promise.pair(schema, sites)
                    .then(new SiteInstanceAdapter());
        }
        return Promise.rejected(new NotFoundException(instanceId.asIri()));
    }

    @Override
    public Promise<Void> persist(Resource resource) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<Integer> countInstances(Criteria criteria) {
        return Promise.rejected(new UnsupportedOperationException());
    }

    @Override
    public Promise<List<FormInstance>> queryInstances(Criteria criteria) {
        return new QueryExecutor(dispatcher, criteria).execute();
    }

    @Override
    public Promise<List<Projection>> query(InstanceQuery query) {
        return new Promise<>(new Joiner(dispatcher, query.getFieldPaths(), query.getCriteria()));
    }
}
