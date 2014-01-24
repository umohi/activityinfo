package org.activityinfo.api2.shared.form;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;

import java.util.List;

/**
 * A group of form fields
 */
public class FormSection implements FormElement, FormElementContainer {


    private final Iri id;
    private LocalizedString label;
    private final List<FormElement> elements = Lists.newArrayList();

    public FormSection(Iri id) {
        this.id = id;
    }

    public Iri getId() {
        return id;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public List<FormElement> getElements() {
        return elements;
    }

    @Override
    public void addElement(FormElement element) {
        elements.add(element);
    }

}
