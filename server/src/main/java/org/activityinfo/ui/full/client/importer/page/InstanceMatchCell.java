package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.activityinfo.ui.full.client.importer.binding.InstanceMatch;


public class InstanceMatchCell extends AbstractCell<InstanceMatch> {

    @Override
    public void render(Context context,
                       InstanceMatch value, SafeHtmlBuilder sb) {

        if (value != null) {
            if (value.isNewInstance()) {
                sb.appendEscaped("New Instance");
            }
        }

    }

}
