package org.activityinfo.ui.full.client.importer.binding;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.data.ImportSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Set of matchers
 */
public class ClassMatcherSet {

    /**
     * Maps FormClass cuid to ClassMatcher
     */
    private Map<Cuid, ClassMatcher> matchers = Maps.newHashMap();

    public ClassMatcherSet(FormTree tree, Map<Integer, FieldPath> columnToFieldPath) {


        Map<FieldPath, Integer> fieldPathToColumn = Maps.newHashMap();
        for(Map.Entry<Integer, FieldPath> entry : columnToFieldPath.entrySet()) {
            fieldPathToColumn.put(entry.getValue(), entry.getKey());
        }

        // given a potentially large input set, we want to match unique combinations of inputs sets to
        // their instance ids.


        List<FieldPath> references = tree.search(FormTree.SearchOrder.DEPTH_FIRST,
                Predicates.alwaysTrue(), FormTree.isReference());

        for(FieldPath fieldPath : references) {
            FormTree.Node node = tree.getNodeByPath(fieldPath);

            Multimap<Integer, Cuid> fieldToColumnMapping = mapColumnToField(node, fieldPathToColumn);
            if(!fieldToColumnMapping.isEmpty()) {
                matcherForClass(node).addMapping(fieldToColumnMapping);
            }
        }
    }

    private ClassMatcher matcherForClass(FormTree.Node node) {
        ClassMatcher classMatcher = matchers.get(node.getFormClass().getId());
        if(classMatcher == null) {
            classMatcher = new ClassMatcher(resourceLocator, node.getFormClass());
            matchers.put(node.getFormClass().getId(), classMatcher);
        }
        return classMatcher;
    }

    private Multimap<Integer, Cuid> mapColumnToField(FormTree.Node node, Map<FieldPath, Integer> fieldPathToColumn) {
        Multimap<Integer, Cuid> fieldToColumn = HashMultimap.create();
        for(FormTree.Node childNode : node.getChildren()) {
            if(!childNode.isReference() && fieldPathToColumn.containsKey(childNode.getPath())) {
                fieldToColumn.put(
                        fieldPathToColumn.get(childNode.getPath()),
                        childNode.getPath().getField().getId());
            }
        }
        return fieldToColumn;
    }

    public void match(ImportSource source) {
        for(int i=0;i!=source.getColumns().size();++i) {
            Set<String> distinctValues = source.distinctValues(i);
            for(ClassMatcher matcher : matchers.values()) {
                matcher.matchColumn(i, distinctValues);
            }
        }
    }
}
