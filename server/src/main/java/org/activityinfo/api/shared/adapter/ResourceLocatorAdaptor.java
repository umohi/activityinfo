package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api.shared.command.Command;
import org.activityinfo.api.shared.command.GetSchema;
import org.activityinfo.api.shared.command.GetSites;
import org.activityinfo.api.shared.command.result.CommandResult;
import org.activityinfo.api.shared.command.result.SiteResult;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api2.client.*;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.api2.shared.form.UserFormInstance;

import javax.annotation.Nullable;

/**
 * Exposes a legacy {@code Dispatcher} implementation as new {@code ResourceLocator}
 */
public class ResourceLocatorAdaptor implements ResourceLocator {

    private final Dispatcher dispatcher;

    public ResourceLocatorAdaptor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Remote<UserForm> getUserForm(Iri formId) {
        if(formId.getScheme().equals(Cuids.SCHEME)) {
            String cuid = formId.getSchemeSpecificPart();
            if(cuid.charAt(0) == CuidAdapter.ACTIVITY_DOMAIN) {
                int activityId = CuidAdapter.getLegacyIdFromCuid(cuid);
                return Remotes.transform(execute(new GetSchema()), new ActivityAdapter(activityId));
            }
        }
        return Remotes.error(new NotFoundException(formId));
    }

    @Override
    public Remote<UserFormInstance> getFormInstance(Iri instanceId) {
        if(instanceId.getScheme().equals(Cuids.SCHEME)) {
            String cuid = instanceId.getSchemeSpecificPart();
            if(cuid.charAt(0) == CuidAdapter.SITE_DOMAIN) {
                int siteId = CuidAdapter.getLegacyIdFromCuid(cuid);
                return Remotes.transform( execute(GetSites.byId(siteId)), new SiteAdapter() );
            }
        }
        return Remotes.error(new NotFoundException(instanceId));
    }

    /**
     * Wraps a legacy command dispatch in a new {@code Remote} object
     *
     * @param command the command to execute
     * @param <R> the type of the {@code Command}'s {@code CommandResult}
     */
    private <R extends CommandResult> Remote<R> execute(final Command<R> command) {
        return new Remote<R>() {

            @Override
            public Promise<R> fetch() {
                try {
                    final Promise<R> promise = new Promise<>();
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
                    return promise;
                } catch(Throwable caught) {
                    return Promise.rejected(caught);
                }
            }
        };
    }

    private static class ActivityAdapter implements Function<SchemaDTO, UserForm> {

        private int activityId;

        private ActivityAdapter(int activityId) {
            this.activityId = activityId;
        }

        @Nullable
        @Override
        public UserForm apply(@Nullable SchemaDTO schemaDTO) {
            ActivityDTO activity = schemaDTO.getActivityById(activityId);
            ActivityUserFormBuilder builder = new ActivityUserFormBuilder(activity);
            return builder.build();
        }
    }

    private static class SiteAdapter implements Function<SiteResult, UserFormInstance>  {

        @Nullable
        @Override
        public UserFormInstance apply(@Nullable SiteResult siteResult) {
            if(siteResult.getData().isEmpty()) {
                throw new NotFoundException();
            }
            return InstanceAdapters.fromSite(siteResult.getData().get(0));
        }
    }

}
