package org.activityinfo.legacy.shared.adapter;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.GetAdminEntities;
import org.activityinfo.legacy.shared.command.GetLocations;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.criteria.*;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.application.FolderClass;
import org.activityinfo.fp.shared.ConcatList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;
import static org.activityinfo.fp.shared.BiFunctions.concatMap;

/**
 * Given an intersection of Criteria, fetch the corresponding entities
 *
 */
public class QueryExecutor  {

    private final Dispatcher dispatcher;
    private final Criteria criteria;

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


    public QueryExecutor(Dispatcher dispatcher, Criteria rootCriteria) {
        this.dispatcher = dispatcher;
        this.criteria = rootCriteria;
        this.criteria.accept(new CriteriaVisitor() {

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
        });
    }

    public Promise<List<FormInstance>> execute() {
        if(classCriteria.size() > 1) {
            // a single instance cannot (at this time) be a member of more than one
            // class, so the result of this query is logically the empty set
            return emptySet();
        }

        if(parentCriteria.size() > 1 ||
                (rootOnly && !parentCriteria.isEmpty())) {
            // likewise, a single instance cannot be a child of multiple parents, so
            // the result of this query is logically the empty set
            return emptySet();
        }

        if(classCriteria.size() == 1) {
            return queryByClassId();

        } else if(ids.size() > 0) {
            List<Promise<List<FormInstance>>> resultSets = Lists.newArrayList();
            for(Character domain : ids.keySet()) {
                resultSets.add(queryByIds(domain, ids.get(domain)));
            }
            return Promise.foldLeft(Collections.<FormInstance>emptyList(), new ConcatList<FormInstance>(), resultSets);

        } else if(rootOnly || !parentCriteria.isEmpty()) {
            Cuid parentId = parentCriteria.iterator().next();
            List<Promise<List<FormInstance>>> lists = Lists.newArrayList();

            if(parentId.getDomain() == DATABASE_DOMAIN || parentId.getDomain() == ACTIVITY_CATEGORY_DOMAIN) {
                return folders();
            } else {
                throw new UnsupportedOperationException("parentID " + parentId);
            }
        } else {
            throw new UnsupportedOperationException("queries must have either class criteria or parent criteria");
        }
    }


    private Promise<List<FormInstance>> queryByIds(char domain, Collection<Integer> ids) {
        switch(domain) {
            case ADMIN_ENTITY_DOMAIN:
                return dispatcher.execute(new GetAdminEntities())
                    .then(new ListResultAdapter<>(new AdminEntityInstanceAdapter()));
            case ATTRIBUTE_DOMAIN:
                return dispatcher.execute(new GetSchema())
                     .then(new AttributeInstanceListAdapter(criteria));

            case LOCATION_DOMAIN:
                return dispatcher.execute(new GetLocations(Lists.newArrayList(ids)))
                        .then(new ListResultAdapter<>(new LocationInstanceAdapter()));

            case DATABASE_DOMAIN:
            case ACTIVITY_CATEGORY_DOMAIN:
            case ACTIVITY_DOMAIN:
            case LOCATION_TYPE_DOMAIN:
                return folders();
        }
        throw new UnsupportedOperationException("unrecognized domain: " + domain);
    }

    private Promise<List<FormInstance>> queryByClassId() {
        if(classCriteria.size() > 1) {
            return emptySet();
        }
        Iri classIri = classCriteria.iterator().next();
        if(!classIri.getScheme().equals(Cuids.SCHEME)) {
            // definitely no matching instances
            return emptySet();
        }

        Cuid formClassId = new Cuid(classIri.getSchemeSpecificPart());

        if(formClassId.equals(FolderClass.CLASS_ID)) {
            return folders();
        }

        switch(formClassId.getDomain()) {
            case ATTRIBUTE_GROUP_DOMAIN:
                return dispatcher.execute(new GetSchema())
                        .then(new AttributeInstanceListAdapter(criteria));

            case ADMIN_LEVEL_DOMAIN:
                return dispatcher.execute(adminQuery(formClassId))
                        .then(new ListResultAdapter<>(new AdminEntityInstanceAdapter()));

            case LOCATION_TYPE_DOMAIN:
                return dispatcher.execute(composeLocationQuery(formClassId))
                        .then(new ListResultAdapter<>(new LocationInstanceAdapter()));

            case PARTNER_FORM_CLASS_DOMAIN:
                return dispatcher.execute(new GetSchema())
                        .then(new PartnerListExtractor(criteria))
                        .then(concatMap(new PartnerInstanceAdapter(formClassId)));

            default:
                return Promise.rejected(new UnsupportedOperationException(
                        "domain not yet implemented: " + formClassId.getDomain()));
        }
    }

    private GetAdminEntities adminQuery(Cuid formClassId) {
        GetAdminEntities query = new GetAdminEntities();
        query.setLevelId(CuidAdapter.getLegacyIdFromCuid(formClassId));
        if(!ids.get(ADMIN_ENTITY_DOMAIN).isEmpty()) {
            query.setEntityIds(ids.get(ADMIN_ENTITY_DOMAIN));
        }
        return query;
    }

    private Promise<List<FormInstance>> folders() {
        return dispatcher.execute(new GetSchema())
                .then(new FolderListAdapter(criteria));
    }

    private GetLocations composeLocationQuery(Cuid formClassId) {
        int locationTypeId = CuidAdapter.getLegacyIdFromCuid(formClassId);
        GetLocations searchLocations = new GetLocations();
        searchLocations.setLocationTypeId(locationTypeId);
        searchLocations.setLocationIds(Lists.newArrayList(ids.get(LOCATION_DOMAIN)));
        return searchLocations;
    }

    private Promise<List<FormInstance>> emptySet() {
        return Promise.resolved(Collections.<FormInstance>emptyList());
    }
}
