package org.activityinfo.legacy.shared.adapter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Cuids;
import org.activityinfo.core.shared.Iri;
import org.activityinfo.core.shared.criteria.*;

import java.util.List;
import java.util.Set;

/**
* Created by alex on 3/15/14.
*/
class CriteriaAnalysis extends CriteriaVisitor {

    /**
     * Instances must be a subclass of all of these FormClasses
     */
    private final Set<Iri> classCriteria = Sets.newHashSet();

    private final Set<Cuid> parentCriteria = Sets.newHashSet();

    private boolean rootOnly = false;

    /**
     * Must be one of these ids
     */
    private final Multimap<Character,Integer> ids = HashMultimap.create();


    public Cuid getParentCriteria() {
        return parentCriteria.iterator().next();
    }

    public boolean isRootOnly() {
        return rootOnly;
    }

    @Override
    public void visitClassCriteria(ClassCriteria criteria) {
        classCriteria.add(criteria.getClassIri());
    }

    @Override
    public void visitInstanceIdCriteria(IdCriteria criteria) {
        // this is implicitly a union criteria
        // separate the instances out into domains
        for (Cuid id : criteria.getInstanceIds()) {
            ids.put(id.getDomain(), CuidAdapter.getLegacyIdFromCuid(id));
        }
    }

    @Override
    public void visitParentCriteria(ParentCriteria criteria) {
        if(criteria.selectsRoot()) {
            rootOnly = true;
        } else {
            parentCriteria.add(criteria.getParentId());
        }
    }

    @Override
    public void visitIntersection(CriteriaIntersection intersection) {
        // A ∩ (B ∩ C) = A ∩ B ∩ C
        for (Criteria criteria : intersection) {
            criteria.accept(this);
        }
    }

    @Override
    public void visitUnion(CriteriaUnion criteria) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmptySet() {
        if(classCriteria.size() > 1) {
            // a single instance cannot (at this time) be a member of more than one
            // class, so the result of this query is logically the empty set
            return true;
        }

        if(parentCriteria.size() > 1 ||
                (rootOnly && !parentCriteria.isEmpty())) {
            // likewise, a single instance cannot be a child of multiple parents, so
            // the result of this query is logically the empty set
            return true;
        }

        if(classCriteria.size() == 1) {
            Iri classIri = classCriteria.iterator().next();
            if(!classIri.getScheme().equals(Cuids.SCHEME)) {
                // definitely no matching instances
                return true;
            }
        }
        return false;
    }

    public boolean isRestrictedToSingleClass() {
        return classCriteria.size() == 1;
    }

    public boolean isRestrictedById() {
        return !ids.isEmpty();
    }

    public boolean isAncestorQuery() {
        return rootOnly || !parentCriteria.isEmpty();
    }

    public Cuid getClassRestriction() {
        Iri classIri = classCriteria.iterator().next();
        return new Cuid(classIri.getSchemeSpecificPart());
    }

    public Multimap<Character, Integer> getIds() {
        return ids;
    }

    public List<Integer> getIds(char domain) {
        return Lists.newArrayList(ids.get(domain));
    }

    public static CriteriaAnalysis analyze(Criteria criteria) {
        CriteriaAnalysis analysis = new CriteriaAnalysis();
        criteria.accept(analysis);
        return analysis;
    }
}
