package org.activityinfo.server.endpoint.odk;

import com.sun.jersey.api.view.Viewable;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.PartnerDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.server.database.hibernate.entity.Partner;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.server.database.hibernate.entity.UserPermission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ListIterator;

@Path("/activityForm")
public class FormResource extends ODKResource {
    @GET @Produces(MediaType.TEXT_XML)
    public Response form(@QueryParam("id") int id) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.finer("ODK activityform " + id + " requested by " +
                     getUser().getEmail() + " (" + getUser().getId() + ")");

        SchemaDTO schemaDTO = dispatcher.execute(new GetSchema());
        ActivityDTO activity = schemaDTO.getActivityById(id);

        if (activity == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        if (!activity.isEditAllowed()) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        return Response.ok(new Viewable("/odk/form.ftl", activity)).build();
    }
}
