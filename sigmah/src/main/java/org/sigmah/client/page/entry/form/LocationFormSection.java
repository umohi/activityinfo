package org.sigmah.client.page.entry.form;

import org.sigmah.shared.dto.LocationDTO;
import org.sigmah.shared.dto.SiteDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LocationFormSection extends FormSection<SiteDTO> {

	void updateForm(LocationDTO location, boolean isNew);
	
	void save(AsyncCallback<Void> callback);

}
