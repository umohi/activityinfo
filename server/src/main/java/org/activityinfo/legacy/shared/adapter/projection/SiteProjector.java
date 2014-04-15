package org.activityinfo.legacy.shared.adapter.projection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.result.ListResult;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.model.PartnerDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;

import java.util.List;

/**
 * The LocationDTO is actually already a projection, so we have a special case adapter which relies on
 * GetLocations for the heavy lifting.
 */
public class SiteProjector implements Function<ListResult<SiteDTO>, List<Projection>> {


    private final List<ProjectionUpdater<LocationDTO>> locationProjectors;
    private final List<ProjectionUpdater<PartnerDTO>> partnerProjectors = Lists.newArrayList();
    private final List<ProjectionUpdater<Double>> indicatorProjectors = Lists.newArrayList();
    private Criteria criteria;

    public SiteProjector(Criteria criteria, List<FieldPath> fields) {
        this.criteria = criteria;
        locationProjectors = LocationProjector.createLocationUpdaters(fields);
        for (FieldPath path : fields) {
            Cuid fieldId = path.getLeafId();

            if (fieldId.getDomain() == CuidAdapter.PARTNER_FORM_CLASS_DOMAIN) {
                int databaseId = CuidAdapter.getBlock(fieldId, 0);
                int fieldIndex = CuidAdapter.getBlock(fieldId, 1);
                partnerProjectors.add(new PartnerProjectionUpdater(path, databaseId, fieldIndex));
            } else if (fieldId.getDomain() == CuidAdapter.INDICATOR_DOMAIN) {
                indicatorProjectors.add(new IndicatorProjectionUpdater(path));
            }
        }
    }

    @Override
    public List<Projection> apply(ListResult<SiteDTO> input) {
        List<Projection> projections = Lists.newArrayList();
        for (SiteDTO site : input.getData()) {
            Projection projection = new Projection(site.getInstanceId(), site.getFormClassId());
            for (ProjectionUpdater<PartnerDTO> projector : partnerProjectors) {
                projector.update(projection, site.getPartner());
            }
            for (ProjectionUpdater<LocationDTO> projector : locationProjectors) {
                projector.update(projection, site.getLocation());
            }

            for (String propertyName : site.getPropertyNames()) {
                if (propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                    Object value = site.get(propertyName);
                    if (value instanceof Number) {
                        final double doubleValue = ((Number) value).doubleValue();
                        for (ProjectionUpdater<Double> projector : indicatorProjectors) {
                            projector.update(projection, doubleValue);
                        }
                    }
                }
            }

            if (criteria.apply(projection)) {
                projections.add(projection);
            }
        }
        return projections;
    }
}
