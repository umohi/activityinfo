package org.activityinfo.api2.client;


import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.api2.shared.form.UserFormInstance;

public interface ResourceLocator {

    /**
     * Fetches the
     *
     * @param formId
     * @return
     */
    Remote<UserForm> getUserForm(Iri formId);

    Remote<UserFormInstance> getFormInstance(Iri formId);

}
