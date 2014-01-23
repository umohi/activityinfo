package org.activityinfo.model.shared.form;

import com.google.common.collect.Lists;
import org.activityinfo.model.LocalizedString;
import org.activityinfo.model.shared.Iri;

import javax.validation.constraints.NotNull;
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
