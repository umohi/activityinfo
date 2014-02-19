package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api2.client.AsyncFunction;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.promises.MapFunction;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Naive implementation that joins and projects a multi-level
 * instance query.
 */
class Joiner implements AsyncFunction<Void, List<Projection>> {


    private final Criteria criteria;
    private Set<FieldPath> fields;
    private List<FieldPath> joinFields;
    private Dispatcher dispatcher;

    public Joiner(Dispatcher dispatcher, List<FieldPath> fields, Criteria criteria) {
        this.dispatcher = dispatcher;

        // find any fields used to do joins
        this.joinFields = joinFields(fields);

        // create a set of all the fields we need to fetch.
        // make sure the join fields are included, otherwise
        // we have to maintain a separate structure for them.
        this.fields = Sets.newHashSet(fields);
        this.fields.addAll(joinFields);

        this.criteria = criteria;
    }

    private List<FieldPath> joinFields(List<FieldPath> paths) {

        // given a set of paths like...
        // territory.province.name
        // territory.province.code
        // territory.name
        // attribute.label
        //
        // we need to find the fields to join on,
        // in increasing order of depth:

        // territory
        // attribute
        // territory.province

        Set<FieldPath> referenceFields = Sets.newHashSet();
        for(FieldPath path : paths) {
            if(path.isNested()) {
                for(int i=1;i<path.getDepth();i++) {
                    referenceFields.add(path.ancestor(i));
                }
            }
        }
        List<FieldPath> ordered = Lists.newArrayList(referenceFields);
        Collections.sort(ordered, new Comparator<FieldPath>() {
            @Override
            public int compare(FieldPath o1, FieldPath o2) {
                return o1.getDepth() - o2.getDepth();
            }
        });

        return ordered;
    }

    @Override
    public void apply(Void noInput, AsyncCallback<List<Projection>> callback) {

        Promise<List<Projection>> results = query(criteria)
                .then(new MapFunction<>(new Project(null)));

        // now schedule the joins
        for(FieldPath fieldToJoin : joinFields) {
            results = results.then(new NestedInstancesFetch(fieldToJoin));
        }

        results.then(callback);
    }


    private Promise<List<FormInstance>> query(Criteria criteria) {
        return new QueryExecutor(dispatcher, criteria).execute();
    }

    private class Project implements Function<FormInstance, Projection> {

        private FieldPath prefix;

        private Project(FieldPath prefix) {
            this.prefix = prefix;
        }

        @Nullable
        @Override
        public Projection apply(@Nullable FormInstance input) {
            Projection projection = new Projection(input.getId());
            for(Map.Entry<Cuid, Object> entry : input.getValueMap().entrySet()) {
                FieldPath path = new FieldPath(prefix, entry.getKey());
                if(fields.contains(path)) {
                    projection.setValue(path, entry.getValue());
                }
            }
            return projection;
        }
    }

    /**
     * Fetches all instances that are referenced by the parent instances
     */
    private class NestedInstancesFetch implements AsyncFunction<List<Projection>, List<Projection>> {

        private FieldPath referenceField;

        public NestedInstancesFetch(FieldPath referenceField) {
            this.referenceField = referenceField;
        }

        @Override
        public void apply(List<Projection> projections, AsyncCallback<List<Projection>> callback) {

            // first collect the ids of the nested FormInstances
            Set<Cuid> instanceIds = Sets.newHashSet();
            for(Projection projection : projections) {
                instanceIds.addAll(projection.getReferenceValue(referenceField));
            }

            if(instanceIds.isEmpty()) {
                callback.onSuccess(projections);
            } else {
                new QueryExecutor(dispatcher, new IdCriteria(instanceIds))
                    .execute()
                    .then(new Join(referenceField, projections))
                    .then(callback);
            }

        }
    }

    private class Join implements Function<List<FormInstance>, List<Projection>> {

        private final FieldPath referenceField;
        private final List<Projection> projections;

        public Join(FieldPath referenceField, List<Projection> projections) {
            this.referenceField = referenceField;
            this.projections = projections;
        }

        @Override
        public List<Projection> apply(List<FormInstance> instances) {
            Map<Cuid, FormInstance> instanceMap = indexJoinedInstances(instances);
            for(Projection projection : projections) {
                Set<Cuid> referencedIds = projection.getReferenceValue(referenceField);
                for(Cuid referencedId : referencedIds) {
                    FormInstance referenceInstance = instanceMap.get(referencedId);
                    if(referenceInstance == null) {
                        throw new IllegalStateException("Missing referenced instance " + referencedId +
                            " (legacy id: " + CuidAdapter.getLegacyIdFromCuid(referencedId) + ") for field " +
                                    referenceField);
                    }
                    populateReferencedFields(projection, referenceInstance);
                }
            }
            return projections;
        }

        private Map<Cuid, FormInstance> indexJoinedInstances(List<FormInstance> instances) {
            Map<Cuid, FormInstance> instanceMap = Maps.newHashMap();
            for(FormInstance instance : instances) {
                instanceMap.put(instance.getId(), instance);
            }
            return instanceMap;
        }

        private void populateReferencedFields(Projection projection, FormInstance referencedInstance) {
            for(Map.Entry<Cuid, Object> entry : referencedInstance.getValueMap().entrySet()) {
                FieldPath path = new FieldPath(referenceField, entry.getKey());
                if(fields.contains(path)) {
                    projection.setValue(path, entry.getValue());
                }
            }
        }
    }
}
