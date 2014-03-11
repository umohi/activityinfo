package org.activityinfo.api.shared.adapter;

import com.google.common.base.Functions;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.adapter.bindings.SiteBinding;
import org.activityinfo.api.shared.adapter.bindings.SiteBindingFactory;
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
import org.activityinfo.api2.shared.Instance;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;

import java.util.List;

import static org.activityinfo.api.shared.adapter.CuidAdapter.*;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final Dispatcher dispatcher;
    private final ClassProvider classProvider;

    public ResourceLocatorAdaptor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.classProvider = new ClassProvider(dispatcher);
    }

    @Override
    public Promise<FormClass> getFormClass(Cuid classId) {
        return classProvider.apply(classId);
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid instanceId) {
        if (instanceId.getDomain() == SITE_DOMAIN) {
            final int siteId = getLegacyIdFromCuid(instanceId);

            Promise<SchemaDTO> schema = dispatcher
                    .execute(new GetSchema());

            Promise<SiteDTO> site = dispatcher
                    .execute(GetSites.byId(siteId))
                    .then(new SingleListResultAdapter<SiteDTO>());

            return Promise.fmap(new SiteInstanceAdapter()).apply(schema, site);
        }
        return Promise.rejected(new NotFoundException(instanceId.asIri()));


    }

    @Override
    public Promise<Void> persist(Instance resource) {
        if(resource instanceof FormInstance) {
            FormInstance instance = (FormInstance) resource;
            if(instance.getId().getDomain() == CuidAdapter.SITE_DOMAIN) {
                int activityId = CuidAdapter.getLegacyIdFromCuid(instance.getClassId());

                Promise<SiteBinding> siteBinding = dispatcher
                        .execute(new GetSchema())
                        .then(new SiteBindingFactory(activityId));

                return Promise.fmap(new SitePersistFunction(dispatcher))
                        .apply(siteBinding, Promise.resolved(instance))
                        .then(Functions.<Void>constant(null));

            }
        }
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
        return new Joiner(dispatcher, query.getFieldPaths(), query.getCriteria()).apply(query);
    }
}
