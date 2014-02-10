package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.InstanceCriteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.ui.full.client.importer.data.ImportRow;
import org.activityinfo.ui.full.client.importer.data.ImportSource;

import java.util.Map;
import java.util.Set;

/**
 * Matches instances of a given FormClass
 */
public class ClassMatcher {

    private final FormClass formClass;
    private final ResourceLocator resourceLocator;

    private Multimap<Integer, Cuid> columnToFieldMap = HashMultimap.create();
    private Multimap<String, Cuid> mapping = HashMultimap.create();


    public ClassMatcher(ResourceLocator resourceLocator, FormClass formClass) {
        this.resourceLocator = resourceLocator;
        this.formClass = formClass;
    }

    public void addMapping(Multimap<Integer, Cuid> columnToFieldMap) {
        this.columnToFieldMap.putAll(columnToFieldMap);
    }

    public void matchColumn(int columnIndex, Set<String> distinctValues) {
        for(Cuid fieldId : columnToFieldMap.get(columnIndex)) {
           resourceLocator.queryInstances(new InstanceCriteria(classes,))
        }
    }
}
