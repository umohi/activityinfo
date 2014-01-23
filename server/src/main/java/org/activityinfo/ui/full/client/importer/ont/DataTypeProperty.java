package org.activityinfo.ui.full.client.importer.ont;


/**
 * Defines a property to which a column could be matched
 *
 * @param <T> the type of the model
 */
public class DataTypeProperty implements Property {

    public enum Type {
        STRING,
        CHOICE,
        DATE
    }

    private String id;
    private String label;
    private Type type;

    public DataTypeProperty(String id, String label, Type type) {
        super();
        this.id = id;
        this.label = label;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }
}
