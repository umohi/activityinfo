package org.activityinfo.ui.full.client.importer.ui.mapping;

/**
 * Placeholder until we have a better way to integrate LESS and gwt styles
 */
public class ColumnMappingStyles {

    public static final ColumnMappingStyles INSTANCE = new ColumnMappingStyles();


    public String page() { return "cm-page"; }

    public String grid() { return "cm-datagrid"; }

    public String sourceColumnHeader() { return "source-column"; }

    public String mappingHeader() { return "mapping"; }

    public String stateIgnored() { return "col-ignored"; }

    public String stateBound() {  return "col-bound"; }

    public String stateUnset() { return "col-unset"; }

    public String selected() {return "col-selected"; }

    public String fieldSelector() { return "cm-field-selector"; }

    public String incomplete() { return "incomplete";  }
}
