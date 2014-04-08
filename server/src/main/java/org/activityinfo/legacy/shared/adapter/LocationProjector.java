package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.legacy.shared.command.result.ListResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.LocationDTO;

import java.util.List;

/**
 * The LocationDTO is actually already a projection, so we have a special case adapter which relies on
 * GetLocations for the heavy lifting.
 */
public class LocationProjector implements Function<ListResult<LocationDTO>, List<Projection>> {

    private final List<Projector> projectors;
    private Criteria criteria;

    private interface Projector {
        void update(Projection projection, LocationDTO locationDTO);
    }

    public LocationProjector(Criteria criteria, List<FieldPath> fields) {
        this.criteria = criteria;
        projectors = Lists.newArrayList();
        for(FieldPath path : fields) {
            Cuid fieldId = path.getLeafId();
            if(fieldId.getDomain() == CuidAdapter.ADMIN_LEVEL_DOMAIN) {
                int levelId = CuidAdapter.getBlock(fieldId, 0);
                int fieldIndex = CuidAdapter.getBlock(fieldId, 1);
                projectors.add(new AdminNameProjector(path, levelId, fieldIndex));

            } else if(fieldId.getDomain() == CuidAdapter.LOCATION_TYPE_DOMAIN) {
                projectors.add(new LocationFieldProjector(
                        path,
                        CuidAdapter.getBlock(fieldId, 1)));
            }
        }
    }

    @Override
    public List<Projection> apply(ListResult<LocationDTO> input) {
        List<Projection> projections = Lists.newArrayList();
        for(LocationDTO location : input.getData()) {
            Projection projection = new Projection(CuidAdapter.locationInstanceId(location.getId()),
                    CuidAdapter.locationFormClass(location.getLocationTypeId()));
            for(Projector projector : projectors) {
                projector.update(projection, location);
            }
            if(criteria.apply(projection)) {
                projections.add(projection);
            }
        }
        return projections;
    }

    private static class LocationFieldProjector implements Projector {
        private FieldPath path;
        private int fieldIndex;

        private LocationFieldProjector(FieldPath path, int fieldIndex) {
            this.path = path;
            this.fieldIndex = fieldIndex;
        }

        @Override
        public void update(Projection projection, LocationDTO locationDTO) {
            switch (fieldIndex) {
                case CuidAdapter.NAME_FIELD:
                    projection.setValue(path, locationDTO.getName());
                    break;
                case CuidAdapter.AXE_FIELD:
                    projection.setValue(path, locationDTO.getAxe());
                    break;
                case CuidAdapter.GEOMETRY_FIELD:
                    if(locationDTO.hasCoordinates()) {
                        projection.setValue(path, new AiLatLng(
                                locationDTO.getLatitude(),
                                locationDTO.getLongitude()));
                    }
                    break;
            }
        }
    }

    private static class AdminNameProjector implements Projector {

        private FieldPath path;
        private int levelId;
        private int fieldIndex;

        private AdminNameProjector(FieldPath path, int levelId, int fieldIndex) {
            this.path = path;
            this.levelId = levelId;
            this.fieldIndex = fieldIndex;
        }

        @Override
        public void update(Projection projection, LocationDTO locationDTO) {
            AdminEntityDTO entity = locationDTO.getAdminEntity(levelId);
            if(entity != null) {
                switch (fieldIndex) {
                    case CuidAdapter.NAME_FIELD:
                        projection.setValue(path, entity.getName());
                        break;
                    case CuidAdapter.CODE_FIELD:
                        // TODO: projection.setValue(path, "");
                        break;
                }
            }
        }
    }
}
