package org.activityinfo.api2.shared.form;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.Resource;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * The basic unit of data collection and storage. A form is similar to a Table
 * but is richer in that it contains instructions to the data collector, validation rules,
 * and potentially relationships with other UserForms.
 * <p/>
 * <p>UserForms are a superset of an OWL class. The user can define them as subclasses or superclasses of
 * another UserForm or RDFS class.</p>
 */
public class UserForm implements Resource, FormElementContainer {

    @NotNull
    private Iri id;
    private LocalizedString label;
    private Iri parentForm;
    private Set<Iri> superClasses = Sets.newHashSet();
    private Set<Iri> subClasses = Sets.newHashSet();
    private List<FormElement> elements = Lists.newArrayList();

    public UserForm(Iri id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public UserForm copy() {
        final UserForm copy = new UserForm(this.getId());
        copy.getElements().addAll(this.getElements());
        copy.getSubClasses().addAll(this.getSubClasses());
        copy.getSuperClasses().addAll(this.getSuperClasses());
        copy.setParentForm(this.getParentForm());
        copy.setLabel(this.getLabel());
        return copy;
    }

    public Iri getId() {
        return id;
    }

    public void setId(Iri id) {
        this.id = id;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public Iri getParentForm() {
        return parentForm;
    }

    public void setParentForm(Iri parentForm) {
        this.parentForm = parentForm;
    }

    public Set<Iri> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(Set<Iri> superClasses) {
        this.superClasses = superClasses;
    }

    public Set<Iri> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(Set<Iri> subClasses) {
        this.subClasses = subClasses;
    }

    public List<FormElement> getElements() {
        return elements;
    }

    public void setElements(List<FormElement> elements) {
        this.elements = elements;
    }

    public void addElement(FormElement element) {
        elements.add(element);
    }
}
