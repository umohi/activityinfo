package org.activityinfo.ui.full.client.importer.ont;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class PropertyPath {

    private final List<Property> path;
    private String key;

    public PropertyPath(List<Property> prefix, Property property) {
        path = Lists.newArrayList();
        path.addAll(prefix);
        path.add(property);
    }

    public PropertyPath(Property... property) {
        path = Arrays.asList(property);
    }

    public PropertyPath(PropertyPath parent, Property property) {
        path = Lists.newArrayList();

        if (parent != null) {
            path.addAll(parent.path);
        }
        path.add(property);
    }

    public String getKey() {
        if (key == null) {
            StringBuilder sb = new StringBuilder();
            for (Property property : path) {
                sb.append(".");
                sb.append(property.getId());
            }
            key = sb.toString();
        }
        return key;
    }

    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        for (Property property : path) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(property.getLabel());
        }
        return sb.toString();
    }

    public DataTypeProperty asDatatypeProperty() {
        return (DataTypeProperty) getProperty();
    }

    public Property getProperty() {
        return path.get(path.size() - 1);
    }

    public ObjectProperty asObjectProperty() {
        return (ObjectProperty) getProperty();
    }

    public PropertyPath child(Property property) {
        return new PropertyPath(path, property);
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
        if (!(obj instanceof PropertyPath)) {
            return false;
        }
        PropertyPath otherPath = (PropertyPath) obj;

        return getKey().equals(otherPath.getKey());
    }
}
