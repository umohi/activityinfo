package org.activityinfo.api2.shared.form.tree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.FormField;

import java.util.List;

/**
 * Describes a path of nested fields
 */
public class FieldPath {

    private final List<Cuid> path;

    public FieldPath(List<FormField> prefix, FormField field) {
        path = Lists.newArrayList();
        for(FormField prefixField : prefix) {
            path.add(prefixField.getId());
        }
        path.add(field.getId());
    }

    public FieldPath(Cuid rootFieldId, FieldPath relativePath) {
        path = Lists.newArrayList(rootFieldId);
        path.addAll(relativePath.path);
    }


    public FieldPath(FieldPath prefix, Cuid key) {
        path = Lists.newArrayList();
        if(prefix != null) {
            path.addAll(prefix.path);
        }
        path.add(key);
    }

    public FieldPath(FormField... fields) {
        path = Lists.newArrayList();
        for(FormField field : fields) {
            path.add(field.getId());
        }
    }


    public FieldPath(FieldPath parent, FieldPath relativePath) {
        this.path = Lists.newArrayList();
        this.path.addAll(parent.path);
        this.path.addAll(relativePath.path);
    }

    public FieldPath(FieldPath parent, FormField field) {
        path = Lists.newArrayList();

        if (parent != null) {
            path.addAll(parent.path);
        }
        path.add(field.getId());
    }

    public FieldPath(List<Cuid> fieldIds) {
        path = Lists.newArrayList(fieldIds);
    }

    public FieldPath(Cuid... fieldIds) {
        path = Lists.newArrayList(fieldIds);
    }


    public boolean isNested() {
        return path.size() > 1;
    }


    public int getDepth() {
        return path.size();
    }

    public FieldPath relativeTo(Cuid rootFieldId) {
        Preconditions.checkArgument(path.get(0).equals(rootFieldId));
        return new FieldPath(path.subList(1, path.size()));
    }

    public Cuid getLeafId() {
        return path.get(path.size()-1);
    }

    /**
     * Creates a new FieldPath that describes the nth ancestor of this
     * path. n=1 is the direct parent, n=2, grand parent, etc.
     */
    public FieldPath ancestor(int n) {
        return new FieldPath(path.subList(0, path.size()-n));
    }

    public Cuid getRoot() {
        return path.get(0);
    }

    public String getLabel() {
        // TODO: remove this method
        return this.toString();
    }

    public boolean isDescendantOf(Cuid fieldId) {
        return path.get(0).equals(fieldId);
    }

    public FieldPath child(FormField field) {
        return new FieldPath(this, field);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(Cuid fieldId : path) {
            if(s.length() > 0) {
                s.append(".");
            }
            s.append(fieldId.asString());
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        return path.hashCode();
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

        return path.equals(otherPath.path);
    }

}
