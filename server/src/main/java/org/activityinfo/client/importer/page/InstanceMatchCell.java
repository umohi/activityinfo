package org.activityinfo.client.importer.page;

import org.activityinfo.client.importer.binding.InstanceMatch;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;


public class InstanceMatchCell extends AbstractCell<InstanceMatch> {

	@Override
	public void render(Context context,
			InstanceMatch value, SafeHtmlBuilder sb) {
		
		if(value != null) {
			if(value.isNewInstance()) {
				sb.appendEscaped("New Instance");
			}
		}
		
	}

}
