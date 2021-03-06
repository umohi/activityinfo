package org.activityinfo.server.endpoint.odk;

import com.extjs.gxt.ui.client.data.RpcMap;
import com.google.inject.Inject;
import com.sun.jersey.multipart.FormDataParam;
import org.activityinfo.legacy.shared.command.CreateLocation;
import org.activityinfo.legacy.shared.command.CreateSite;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.server.database.hibernate.dao.Geocoder;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import org.activityinfo.server.database.hibernate.entity.AdminLevel;
import org.activityinfo.server.endpoint.odk.SiteFormData.FormAttributeGroup;
import org.activityinfo.server.endpoint.odk.SiteFormData.FormIndicator;
import org.activityinfo.server.event.sitehistory.SiteHistoryProcessor;
import org.activityinfo.legacy.client.KeyGenerator;

import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/submission")
public class FormSubmissionResource extends ODKResource {
    private final Provider<FormParser> formParser;
    private final Geocoder geocoder;
    private final SiteHistoryProcessor siteHistoryProcessor;

    @Inject
    public FormSubmissionResource(Provider<FormParser> formParser, Geocoder geocoder,
                                  SiteHistoryProcessor siteHistoryProcessor) {
        this.formParser = formParser;
        this.geocoder = geocoder;
        this.siteHistoryProcessor = siteHistoryProcessor;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_XML)
    public Response submit(@FormDataParam("xml_submission_file") String xml) throws Exception {
        if (enforceAuthorization()) {
            return askAuthentication();
        }
        LOGGER.fine("ODK form submitted by user " + getUser().getEmail() + " (" + getUser().getId() + ")");

        // parse
        SiteFormData data = formParser.get().parse(xml);
        if (data == null) {
            return badRequest("Problem parsing submission XML");
        }

        // basic validation
        if (data.getActivity() == 0 || data.getPartner() == 0 ||
                data.getLatitude() == 999 || data.getLongitude() == 999 ||
                data.getDate1() == null || data.getDate2() == null || data.getDate2().before(data.getDate1())) {
            return badRequest("Problem validating submission XML");
        }

        // check if activity exists
        SchemaDTO schemaDTO = dispatcher.execute(new GetSchema());
        ActivityDTO activity = schemaDTO.getActivityById(data.getActivity());
        if (activity == null) {
            return notFound("Unknown activity");
        }

        // create site
        try {
            createSite(data, schemaDTO, activity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }
        return Response.status(Status.CREATED).build();
    }

    private void createSite(SiteFormData data, SchemaDTO schemaDTO, ActivityDTO activity) {
        final SiteDTO site = new SiteDTO();
        site.setId(new KeyGenerator().generateInt());
        site.setActivityId(data.getActivity());

        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            site.setReportingPeriodId(new KeyGenerator().generateInt());
        }

        // set activitymodel
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            site.setDate1(data.getDate1());
            site.setDate2(data.getDate2());
        }
        site.setPartner(schemaDTO.getPartnerById(data.getPartner()));

        // set comments
        site.setComments(data.getComments());

        // set attributes
        for (FormAttributeGroup formAttributeGroup : data.getAttributegroups()) {
            AttributeGroupDTO attributeGroup = activity.getAttributeGroupById(formAttributeGroup.getId());
            for (Integer attributeId : attributeGroup.getAttributeIds()) {
                site.setAttributeValue(attributeId, formAttributeGroup.isSelected(attributeId));
            }
        }

        // set indicators
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            for (FormIndicator formIndicator : data.getIndicators()) {
                site.setIndicatorValue(formIndicator.getId(), formIndicator.getDoubleValue());
            }
        }

        // create command(s)
        CreateSite cmd = new CreateSite(site);
        cmd.setNestedCommand(createCreateLocationCommand(data, schemaDTO, activity));

        // save
        CreateResult createResult = dispatcher.execute(cmd);

        // create sitehistory entry
        siteHistoryProcessor.process(cmd, getUser().getId(), createResult.getNewId());
    }

    private CreateLocation createCreateLocationCommand(SiteFormData data, SchemaDTO schemaDTO, ActivityDTO activity) {
        // create the dto
        LocationDTO loc = new LocationDTO();
        loc.setId(new KeyGenerator().generateInt());
        loc.setLocationTypeId(activity.getLocationTypeId());
        loc.setName(data.getLocationname());
        loc.setLatitude(data.getLatitude());
        loc.setLongitude(data.getLongitude());

        CreateLocation cmd = new CreateLocation(loc);

        // get adminentities that contain the specified coordinates
        List<AdminEntity> adminentities =
                geocoder.geocode(data.getLatitude(), data.getLongitude());
        if (adminentities.isEmpty()) {
            AdminEntity adminEntity = createDebugAdminEntity();
            if (adminEntity != null) {
                adminentities.add(adminEntity);
            }
        }
        if (!adminentities.isEmpty()) {
            RpcMap map = cmd.getProperties();
            for (AdminEntity entity : adminentities) {
                map.put(AdminLevelDTO.getPropertyName(entity.getLevel().getId()), entity.getId());
            }
        }

        return cmd;
    }

    private AdminEntity createDebugAdminEntity() {
        AdminEntity adminEntity = null;

        String odkDebugLocationEntityId = config.getProperty("odk.debug.location.entity.id");
        String odkDebugLocationLevelId = config.getProperty("odk.debug.location.level.id");

        if (odkDebugLocationEntityId != null && odkDebugLocationLevelId != null) {
            AdminLevel level = new AdminLevel();
            level.setId(Integer.parseInt(odkDebugLocationLevelId));
            adminEntity = new AdminEntity();
            adminEntity.setId(Integer.parseInt(odkDebugLocationEntityId));
            adminEntity.setLevel(level);
        }
        return adminEntity;
    }
}
