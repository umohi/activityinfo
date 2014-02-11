package org.activityinfo.api.shared.adapter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.command.*;
import org.activityinfo.api.shared.command.result.CommandResult;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.SiteDTO;
import org.activityinfo.api2.client.NotFoundException;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.Resource;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;

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
                return execute(new GetSchema()).then(new ActivityAdapter(activityId));

            case PARTNER_FORM_CLASS_DOMAIN:
                return Promise.resolved(PartnerClassAdapter.create(getLegacyIdFromCuid(formId)));

            case PROJECT_DOMAIN:
                return Promise.resolved(BuiltinFormClasses.projectFormClass(getLegacyIdFromCuid(formId)));

            case ATTRIBUTE_GROUP_DOMAIN:
                return execute(new GetSchema()).then(new AttributeClassAdapter(getLegacyIdFromCuid(formId)));

            case ADMIN_LEVEL_DOMAIN:
                return execute(new GetSchema()).then(new AdminLevelClassAdapter(getLegacyIdFromCuid(formId)));

            case LOCATION_TYPE_DOMAIN:
                return execute(new GetSchema()).then(new LocationClassAdapter(getLegacyIdFromCuid(formId)));

            default:
                return Promise.rejected(new NotFoundException(formId.asIri()));
        }
    }

    @Override
    public Promise<FormInstance> getFormInstance(Cuid instanceId) {
        if (instanceId.getDomain() == SITE_DOMAIN) {
            final int siteId = getLegacyIdFromCuid(instanceId);

            return new Promise<>(new Promise.AsyncOperation<FormInstance>() {
                @Override
                public void start(final Promise<FormInstance> promise) {
                    // execute via dispatcher directly to avoi promise.resolve(),
                    // we want to resolve it when sub-call is finished
                    dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            promise.reject(throwable);
                        }

                        @Override
                        public void onSuccess(final SchemaDTO schemaDTO) {
                            execute(GetSites.byId(siteId)).
                                    then(new SingleListResultAdapter<SiteDTO>()).
                                    then(new SiteInstanceAdapter(schemaDTO)).
                                    then(new AsyncCallback<FormInstance>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                            promise.reject(caught);
                                        }

                                        @Override
                                        public void onSuccess(FormInstance result) {
                                            promise.resolve(result);
                                        }
                                    });
                        }
                    });
                }
            });
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

        // the backend for api1 is not so sophisticated, so we fetch all relevant instances
        // and do the final filter on this end.

        if (criteria instanceof ClassCriteria) {
            return queryInstances(((ClassCriteria) criteria).getClassIri());
        }

        return Promise.rejected(new UnsupportedOperationException());
    }

    /**
     * Query all the instances of a single FormClass
     *
     * @param classIri
     * @return a {@code Promise} to a {@code List} of FormInstances
     */
    private Promise<List<FormInstance>> queryInstances(Iri classIri) {
        if (!classIri.getScheme().equals(Cuids.SCHEME)) {
            return Promise.rejected(new UnsupportedOperationException("iri: " + classIri));
        }

        Cuid formClassId = new Cuid(classIri.getSchemeSpecificPart());
        switch (formClassId.getDomain()) {
            case ATTRIBUTE_GROUP_DOMAIN:
                return execute(new GetSchema())
                        .then(new AttributeInstanceListAdapter(formClassId));

            case ADMIN_LEVEL_DOMAIN:
                return execute(new GetAdminEntities(CuidAdapter.getLegacyIdFromCuid(formClassId)))
                        .then(new ListResultAdapter<>(new AdminEntityInstanceAdapter()));

            case LOCATION_TYPE_DOMAIN:
                return execute(new SearchLocations())
                        .then(new ListResultAdapter<>(new LocationInstanceAdapter(formClassId)));

            case PARTNER_FORM_CLASS_DOMAIN:
                return execute(new GetSchema())
                        .then(new PartnerListExtractor(formClassId))
                        .then(new ListTransformer<>(new PartnerInstanceAdapter(formClassId)));

            default:
                return Promise.rejected(new UnsupportedOperationException(
                        "domain not yet implemented: " + formClassId.getDomain()));
        }
    }


    /**
     * Wraps a legacy command dispatch in a new {@code Remote} object
     *
     * @param command the command to execute
     * @param <R>     the type of the {@code Command}'s {@code CommandResult}
     */
    private <R extends CommandResult> Promise<R> execute(final Command<R> command) {
        return new Promise<R>(new Promise.AsyncOperation<R>() {

            @Override
            public void start(final Promise<R> promise) {
                try {
                    dispatcher.execute(command, new AsyncCallback<R>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            promise.reject(throwable);
                        }

                        @Override
                        public void onSuccess(R result) {
                            promise.resolve(result);
                        }
                    });
                } catch (Throwable caught) {
                    promise.reject(caught);
                }
            }
        });
    }

}
