package org.activityinfo.geoadmin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.activityinfo.geoadmin.model.AdminEntity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Process for matching imported features to a set of existing administrative
 * entities.
 */
public class Joiner {

    /**
     * The minimum score for a match by name. Below this we just get garbage.
     */
    private static final double MIN_NAME_MATCH = 0.75;

    /**
     * The threshold of a very sure match geographically.
     */
    private static final double SURE_GEO_MATCH = 0.95;

    private List<AdminEntity> entities;
    private List<ImportFeature> features;

    public Joiner(List<AdminEntity> entities, List<ImportFeature> features) {
        this.entities = entities;
        this.features = features;
    }

    /**
     * Joins the imported features to their probable parents among the list of
     * admin entities.
     * 
     * @return a list of entities, where each item in the list is the best
     *         matching parent for the corresponding item in the features list.
     */
    public List<AdminEntity> joinParents() {
        List<AdminEntity> parents = Lists.newArrayList();
        for (ImportFeature feature : features) {
            parents.add(findBestMatch(feature));
        }
        return parents;
    }

    /**
     * Joins the imported features on a one-to-one basis with the list of admin
     * entities.
     */
    public List<Join> joinOneToOne() {

        List<Join> joins = Lists.newArrayList();
        Set<ImportFeature> unmatchedFeatures = Sets.newHashSet(features);
        Set<AdminEntity> unmatchedEntities = Sets.newHashSet(entities);

        // in the first pass, match strictly by name
        for (AdminEntity unit : entities) {
            ImportFeature match = matchExactlyByName(unit);
            if (match != null && unmatchedEntities.contains(match)) {
                joins.add(new Join(unit, match));
                unmatchedEntities.remove(unit);
                unmatchedFeatures.remove(match);
            }
        }

        // now match remaining elements fuzzily

        for (AdminEntity entity : Lists.newArrayList(unmatchedEntities)) {
            ImportFeature match = findBestMatch(entity, unmatchedFeatures);
            if (match != null) {
                joins.add(new Join(entity, match));
                unmatchedEntities.remove(entity);
                unmatchedFeatures.remove(match);
            }
        }

        // and finally and unmatched ones as loners
        for (AdminEntity entity : unmatchedEntities) {
            joins.add(new Join(entity, null));
        }

        for (ImportFeature feature : unmatchedFeatures) {
            joins.add(new Join(null, feature));
        }

        sortJoinsByAdminName(joins);

        return joins;
    }

    private void sortJoinsByAdminName(List<Join> joins) {
        Collections.sort(joins, new Comparator<Join>() {

            @Override
            public int compare(Join o1, Join o2) {
                String s1 = "ZZZZ";
                String s2 = "ZZZZ";
                if (o1.getEntity() != null) {
                    s1 = o1.getEntity().getName();
                }
                if (o2.getEntity() != null) {
                    s2 = o2.getEntity().getName();
                }
                return s1.compareTo(s2);
            }

        });
    }

    /**
     * Match the admin entity to the closest element in the collection of
     * features
     * 
     * @param unit
     * @param features
     * @return
     */
    private ImportFeature findBestMatch(AdminEntity unit,
        Iterable<ImportFeature> features) {
        double bestScore = 0;
        ImportFeature bestFeature = null;
        for (ImportFeature feature : features) {

            double nameSimilarity = scoreName(unit, feature);
            double geoScore = calculateOverlap(unit, feature);

            // avoid totally spurious matches...
            if (nameSimilarity > MIN_NAME_MATCH || geoScore > SURE_GEO_MATCH) {

                double totalScore = nameSimilarity + geoScore;

                if (totalScore > bestScore) {
                    bestScore = totalScore;
                    bestFeature = feature;
                }
            }
        }
        return bestFeature;
    }

    /**
     * Matches exactly by the name, returning a value if EXACTLY one feature
     * matches the name, otherwise null.
     */
    private ImportFeature matchExactlyByName(AdminEntity entity) {
        Set<ImportFeature> exactMatches = Sets.newHashSet();
        for (ImportFeature feature : features) {
            if (feature.similarity(entity.getName()) == 1.0) {
                exactMatches.add(feature);
            }
        }
        if (exactMatches.size() == 1) {
            return exactMatches.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Finds the best match
     * 
     * @param feature
     * @return
     */
    private AdminEntity findBestMatch(ImportFeature feature) {
        double bestScore = 0;
        AdminEntity bestEntity = null;
        for (AdminEntity entity : entities) {
            double score = scoreJoin(entity, feature);
            if (score > bestScore) {
                bestScore = score;
                bestEntity = entity;
            }
        }
        return bestEntity;
    }

    public double scoreJoin(AdminEntity entity, ImportFeature feature) {
        // calculate the proportion of overlap with this admin unit
        double geoOverlap = calculateOverlap(entity, feature);

        // calculate the name overlap
        double nameSimilarity = scoreName(entity, feature);

        // give a bonus for perfect name match
        if (nameSimilarity == 1.0) {
            nameSimilarity += 0.25;
        }

        System.out.println(String.format("%s <> %s %.2f %.2f", entity.getName(),
            feature.toString(),
            geoOverlap, nameSimilarity));

        return nameSimilarity + geoOverlap;
    }

    /**
     * Calculates the proportion of overlap between the imported feature and the
     * existing entity.
     * 
     * @return score from 0=no overlap, 1=perfect overlap
     */
    public static double calculateOverlap(AdminEntity unit, ImportFeature feature) {
        if (unit.getBounds() == null) {
            return 0;
        }
        Envelope unitEnvelope = GeoUtils.toEnvelope(unit.getBounds());
        Envelope featureEnvelope = feature.getEnvelope();
        double geoOverlap = unitEnvelope.intersection(featureEnvelope).getArea() /
            unitEnvelope.getArea();
        return geoOverlap;
    }

    public static double scoreName(AdminEntity entity, ImportFeature feature) {
        return feature.similarity(entity.getName());
    }

}
