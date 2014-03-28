package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.legacy.shared.command.result.ListResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;

import java.util.List;

/**
 * The LocationDTO is actually already a projection, so we have a special case adapter which relies on
 * GetLocations for the heavy lifting.
 */
public class SiteProjector implements Function<ListResult<SiteDTO>, List<Projection>> {

    private final List<Projector> projectors;

    private interface Projector {
        void update(Projection projection, SiteDTO site);
    }

    public SiteProjector(List<FieldPath> fields) {
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
    public List<Projection> apply(ListResult<SiteDTO> input) {
        List<Projection> projections = Lists.newArrayList();
        for(SiteDTO site : input.getData()) {
            Projection projection = new Projection(CuidAdapter.locationInstanceId(site.getId()),
                    CuidAdapter.activityFormClass(site.getActivityId()));
            for(Projector projector : projectors) {
                projector.update(projection, site);
            }
            projections.add(projection);
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
        public void update(Projection projection, SiteDTO site) {
            switch (fieldIndex) {
                case CuidAdapter.NAME_FIELD:
                    projection.setValue(path, site.getLocationName());
                    break;
                case CuidAdapter.AXE_FIELD:
                    projection.setValue(path, site.getLocationAxe());
                    break;
                case CuidAdapter.GEOMETRY_FIELD:
                    if(site.hasLatLong()) {
                        projection.setValue(path, new AiLatLng(
                                site.getLatitude(),
                                site.getLongitude()));
                    }
                    break;
            }
        }
    }

    private static class PartnerProjector implements Projector {

        @Override
        public void update(Projection projection, SiteDTO site) {

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
        public void update(Projection projection, SiteDTO site) {
            AdminEntityDTO entity = site.getAdminEntity(levelId);
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