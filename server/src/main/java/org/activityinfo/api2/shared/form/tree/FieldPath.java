package org.activityinfo.api2.shared.form.tree;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;

import java.util.Arrays;
import java.util.List;

/**
 * Describes a path of nested fields
 */
public class FieldPath {

    private final List<FormField> path;
    private String key;

    public FieldPath(List<FormField> prefix, FormField field) {
        path = Lists.newArrayList();
        path.addAll(prefix);
        path.add(field);
    }

    public FieldPath(FormField... field) {
        path = Arrays.asList(field);
    }

    public FieldPath(FieldPath parent, FormField field) {
        path = Lists.newArrayList();

        if (parent != null) {
            path.addAll(parent.path);
        }
        path.add(field);
    }

    public String getKey() {
        if (key == null) {
            StringBuilder sb = new StringBuilder();
            for (FormField field : path) {
                sb.append(".");
                sb.append(field.getId());
            }
            key = sb.toString();
        }
        return key;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        for (FormField field : path) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(field.getLabel());
        }
        return sb.toString();
    }

    public FormField getField() {
        return path.get(path.size() - 1);
    }

    public boolean isReference() {
        return getField().getType() == FormFieldType.REFERENCE;
    }

    public FieldPath child(FormField field) {
        return new FieldPath(path, field);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(FormField field : path) {
            if(s.length() > 0) {
                s.append(".");
            }
            String label = field.getLabel().getValue();
            if(label.contains(" ")) {
                s.append("[").append(label).append("]");
            } else {
                s.append(label);
            }
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FieldPath)) {
            return false;
        }
        FieldPath otherPath = (FieldPath) obj;

        return getKey().equals(otherPath.getKey());
    }

}
