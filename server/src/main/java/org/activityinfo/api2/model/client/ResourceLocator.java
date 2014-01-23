package org.activityinfo.api2.model.client;


import org.activityinfo.api2.model.shared.Iri;
import org.activityinfo.api2.model.shared.form.FormInstance;
import org.activityinfo.api2.model.shared.form.UserForm;

public interface ResourceLocator {

    /**
     * Fetches the
     *
     * @param formId
     * @return
     */
    Remote<UserForm> getUserForm(Iri formId);

    Remote<Iterable<FormInstance>> getInstances(Iri formId);
}
