package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.core.shared.Cuid;

/**
 * Some fields can have multiple binding "sites" to which columns
 * can be bound. For example, a GeographicPoint can accept two columns -
 * a latitude and a longitude - as an import source.
 */
public class ImportTarget {

    private final Cuid fieldId;
    private final TargetSiteId site;
    private final String label;
    private final Cuid formClassId;

    public ImportTarget(Cuid fieldId, TargetSiteId site, String label, Cuid formClassId) {
        this.fieldId = fieldId;
        this.site = site;
        this.label = label;
        this.formClassId = formClassId;
    }

    public Cuid getFormClassId() {
        return formClassId;
    }

    public Cuid getFieldId() {
        return fieldId;
    }

    public TargetSiteId getSite() {
        return site;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + "[" + site + "@" + fieldId + "]";
    }
}
