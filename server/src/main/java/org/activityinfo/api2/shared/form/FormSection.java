package org.activityinfo.api2.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A logical group of form fields
 */
public class FormSection implements FormElement, FormElementContainer {

    public static final Cuid CLASS_ID = new Cuid("_form_section");

    private final Cuid id;
    private LocalizedString label;
    private final List<FormElement> elements = Lists.newArrayList();

    public FormSection(Cuid id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public Cuid getId() {
        return id;
    }

    @Override
    public Cuid getClassId() {
        return CLASS_ID;
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
