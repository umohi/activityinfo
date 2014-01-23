package org.activityinfo.model.shared.form;


import java.util.List;

public interface FormElementContainer {

    List<FormElement> getElements();

    void addElement(FormElement element);
}
