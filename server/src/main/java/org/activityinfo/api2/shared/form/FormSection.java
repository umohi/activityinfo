package org.activityinfo.api2.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;

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
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public Iri getId() {
        return id;
    }

    @NotNull
    public LocalizedString getLabel() {
        return LocalizedString.nullToEmpty(label);
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

    @Override
    public String toString() {
        return "FormSection{" +
                "id=" + id +
                ", label=" + label +
                '}';
    }
}
