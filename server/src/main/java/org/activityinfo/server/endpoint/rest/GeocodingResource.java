package org.activityinfo.server.endpoint.rest;

import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.api2.shared.model.AiLatLng;
import org.activityinfo.server.database.hibernate.dao.Geocoder;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import org.activityinfo.server.database.hibernate.entity.AdminEntityViews;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class GeocodingResource {

    @GET
    @JsonView(AdminEntityViews.GeocodeView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public List<AdminEntity> geocode(@InjectParam Geocoder geocoder,
                                     @QueryParam("lat") double latitude,
                                     @QueryParam("lng") double longitude) {

        return geocoder.geocode(latitude, longitude);
    }

    @POST
    @JsonView(AdminEntityViews.GeocodeView.class)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<List<AdminEntity>> geocodeBatch(@InjectParam Geocoder geocoder,
                                                List<AiLatLng> points) {

        return geocoder.gecodeBatch(points);
    }


}
