package org.activityinfo.api2.shared.form;

import com.google.common.base.Preconditions;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The smallest logical unit of data entry. A single field can yield
 * multiple RDFS properties.
 */
public class FormField implements FormElement {

    private final Iri id;
    private LocalizedString label;
    private LocalizedString description;
    private LocalizedString unit;
    private FormFieldType type;
    private Set<Iri> range;
    private String calculation;
    private boolean readOnly;
    private boolean visible = true;
    private List<Iri> dimensions;
    private boolean required;
    private FormFieldCardinality cardinality;
    private List<FormFieldEnumValue> enumValues;

    public FormField(Iri id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    public List<FormFieldEnumValue> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<FormFieldEnumValue> enumValues) {
        this.enumValues = enumValues;
    }

    public FormFieldCardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(FormFieldCardinality cardinality) {
        this.cardinality = cardinality;
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

    /**
     * @return an extended description of this field, presented to be
     * presented to the user during data entry
     */
    @NotNull
    public LocalizedString getDescription() {
        return LocalizedString.nullToEmpty(description);
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    @NotNull
    public LocalizedString getUnit() {
        return LocalizedString.nullToEmpty(unit);
    }

    public void setUnit(LocalizedString unit) {
        this.unit = unit;
    }

    /**
     * @return this field's type
     */
    public FormFieldType getType() {
        return type;
    }

    public void setType(FormFieldType type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public Set<Iri> getRange() {
        return range;
    }

    public void setRange(Set<Iri> range) {
        this.range = range;
    }

    public void setRange(Iri range) {
        this.range = Collections.singleton(range);
    }

    /**
     *
     * @return true if this field requires a response before submitting the form
     */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return the expression used to calculate this field's value if it is
     * not provided by the user
     */
    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    /**
     * @return true if this field is read-only.
     */
    boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return true if this field is visible to the user
     */
    boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<Iri> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Iri> dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormField formField = (FormField) o;

        if (id != null ? !id.equals(formField.id) : formField.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FormField{" +
                "id=" + id +
                ", label=" + label +
                ", type=" + type +
                '}';
    }
}
