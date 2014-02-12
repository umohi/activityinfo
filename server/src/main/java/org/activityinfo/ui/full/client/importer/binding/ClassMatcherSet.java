package org.activityinfo.ui.full.client.importer.binding;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;

import java.util.List;
import java.util.Map;

/**
 * Set of matchers
 */
public class ClassMatcherSet {

    private final ResourceLocator resourceLocator;

    /**
     * Maps FormClass cuid to ReferenceMatcher
     */
    private final List<ReferenceMatcher> matchers = Lists.newArrayList();

    public ClassMatcherSet(ResourceLocator resourceLocator, FormTree tree,
                           Map<Integer, FieldPath> columnToFieldPath) {

        this.resourceLocator = resourceLocator;

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

            ReferenceMatcher matcher = new ReferenceMatcher(resourceLocator, node, fieldPathToColumn);
            if(matcher.hasSourceMapping()) {
                matchers.add(matcher);
            }
        }
    }

    public List<ReferenceMatcher> getMatchers() {
        return matchers;
    }
}
