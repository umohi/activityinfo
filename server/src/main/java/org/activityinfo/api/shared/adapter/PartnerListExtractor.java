package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.activityinfo.api.shared.model.PartnerDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.api2.shared.Cuid;

import java.util.Collections;
import java.util.List;

/**
 * Extracts a list a given database's Partners from the SchemaDTO.
 */
public class PartnerListExtractor implements Function<SchemaDTO, List<PartnerDTO>> {

    private final int databaseId;

    public PartnerListExtractor(Cuid formClassId) {
        Preconditions.checkArgument(formClassId.getDomain() == CuidAdapter.PARTNER_FORM_CLASS_DOMAIN);
        this.databaseId = CuidAdapter.getLegacyIdFromCuid(formClassId);
    }

    @Override
    public List<PartnerDTO> apply(SchemaDTO input) {
        UserDatabaseDTO database = input.getDatabaseById(this.databaseId);
        if(database == null) {
            return Collections.emptyList();
        } else {
            return database.getPartners();
        }
    }
}
