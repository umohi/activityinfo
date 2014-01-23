package org.activityinfo.model.client;


import org.activityinfo.model.shared.Iri;
import org.activityinfo.model.shared.form.FormInstance;
import org.activityinfo.model.shared.form.UserForm;

import java.util.Collection;
import java.util.Set;

public interface ResourceLocator {

    /**
     * Fetches the
     * @param formId
     * @return
     */
    Remote<UserForm> getUserForm(Iri formId);

    Remote<Iterable<FormInstance>> getInstances(Iri formId);
}
