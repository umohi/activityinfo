package org.activityinfo.ui.full.client.importer.columns;

import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.ui.full.client.importer.draft.DraftInstance;
import org.activityinfo.ui.full.client.importer.match.ValueStatus;

/**
 *
 */
public interface DraftColumn {

    LocalizedString getHeader();

    Object getValue(DraftInstance instance);

    ValueStatus getStatus(DraftInstance instance);
}
