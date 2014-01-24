package org.activityinfo.api2.client;


import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.UserForm;

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
