package org.activityinfo.api2.client;


import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.api2.shared.form.UserFormInstance;

public interface ResourceLocator {

    /**
     * Fetches the user form.
     *
     * @param formId
     * @return
     */
    Remote<UserForm> getUserForm(Iri formId);

    Remote<UserFormInstance> getFormInstance(Iri formId);

    Promise<Iri> createFormInstance(UserFormInstance formInstance);

    Promise<Boolean> saveFormInstance(UserFormInstance formInstance);

    Promise<UserForm> createUserForm();

    Promise<Boolean> saveUserForm(UserForm userForm);

}
