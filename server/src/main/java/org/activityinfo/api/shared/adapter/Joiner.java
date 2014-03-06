package org.activityinfo.api.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.api.client.Dispatcher;
import org.activityinfo.api2.client.InstanceQuery;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.criteria.IdCriteria;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;

import javax.annotation.Nullable;
import java.util.*;

import static org.activityinfo.api2.shared.function.BiFunctions.concatMap;

/**
 * Naive implementation that joins and projects a multi-level
 * instance query.
 */
class Joiner implements Function<InstanceQuery, Promise<List<Projection>>> {


    private final Criteria criteria;
    private final ClassProvider classProvider;
    private Set<FieldPath> fields;
    private List<FieldPath> joinFields;
    private Dispatcher dispatcher;
    private Map<Cuid, FormClass> classMap = Maps.newHashMap();

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

        this.classProvider = new ClassProvider(dispatcher);
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
    public Promise<List<Projection>> apply(InstanceQuery instanceQuery) {

        Promise<List<Projection>> results =
                query(criteria)
                .join(new FetchClassDefinitions())
                .then(concatMap(new ProjectFunction(null)));

        // now schedule the joins
        for(FieldPath fieldToJoin : joinFields) {
            results = results.join(new FetchAndJoinFunction(fieldToJoin));
        }

        return results;
    }

    private Promise<List<FormInstance>> query(Criteria criteria) {
        return new QueryExecutor(dispatcher, criteria).execute();
    }


    private class ProjectFunction implements Function<FormInstance, Projection> {

        private FieldPath prefix;

        private ProjectFunction(FieldPath prefix) {
            this.prefix = prefix;
        }

        @Nullable
        @Override
        public Projection apply(FormInstance input) {
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

    private class FetchClassDefinitions implements Function<List<FormInstance>, Promise<List<FormInstance>>> {

        @Override
        public Promise<List<FormInstance>> apply(final List<FormInstance> formInstances) {

            Set<Cuid> classesToFetch = Sets.newHashSet();
            for(FormInstance instance : formInstances) {
                if(!classMap.containsKey(instance.getClassId())) {
                    classesToFetch.add(instance.getClassId());
                }
            }

            Promise<Void> promise = Promise.resolved(null);
            for(final Cuid classToFetch : classesToFetch) {

                promise = promise.join(new Function<Void, Promise<Void>>() {

                    @Nullable
                    @Override
                    public Promise<Void> apply(@Nullable Void input) {
                        return classProvider.get(classToFetch).then(new IndexClassDefinition());
                    }
                });
            }

            return promise.then(Functions.constant(formInstances));
        }
    }

    private class IndexClassDefinition implements Function<FormClass, Void> {

        @Nullable
        @Override
        public Void apply(FormClass input) {
            classMap.put(input.getId(), input);
            return null;
        }
    }

    /**
     * Fetches all instances that are referenced by the parent instances
     */
    private class FetchAndJoinFunction implements Function<List<Projection>, Promise<List<Projection>>> {

        private FieldPath referenceField;

        public FetchAndJoinFunction(FieldPath referenceField) {
            this.referenceField = referenceField;
        }

        @Override
        public Promise<List<Projection>> apply(List<Projection> projections) {

            // first collect the ids of the nested FormInstances
            Set<Cuid> instanceIds = Sets.newHashSet();
            for(Projection projection : projections) {
                instanceIds.addAll(projection.getReferenceValue(referenceField));
            }

            if(instanceIds.isEmpty()) {
                return Promise.resolved(projections);
            } else {
                return new QueryExecutor(dispatcher, new IdCriteria(instanceIds))
                    .execute()
                    .then(new JoinFunction(referenceField, projections));
            }
        }
    }

    private class JoinFunction implements Function<List<FormInstance>, List<Projection>> {

        private final FieldPath referenceField;
        private final List<Projection> projections;

        public JoinFunction(FieldPath referenceField, List<Projection> projections) {
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
