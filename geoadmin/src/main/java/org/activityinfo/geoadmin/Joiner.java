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
    	
    	// do the features contain the parent's names at all?
    	double nameWeight =
    			probabilityThatFeaturesContainParentNames();
    	
    	System.out.println("Probability that features contain parent names = " + nameWeight);
    	
        List<AdminEntity> parents = Lists.newArrayList();
        for (ImportFeature feature : features) {
            parents.add(findBestMatchingParentEntity(feature, nameWeight));
        }
        return parents;
    }

    private double probabilityThatFeaturesContainParentNames() {
		double sum = 0;
    	for(ImportFeature feature : features) {
			
			double bestMatch = 0;
			for(AdminEntity parent : entities) {
				double match = feature.similarity(parent.getName());
				if(match > bestMatch) {
					bestMatch = match;
				}
				if(bestMatch == 1.0) {
					break;
				}
			}
			if(bestMatch > 0.8) {
				sum += bestMatch;
			}
			
		}
    	return sum / (double)features.size();
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
     * @param entity
     * @param features
     * @return
     */
    private ImportFeature findBestMatch(AdminEntity entity,
        Iterable<ImportFeature> features) {
        double bestScore = 0;
        ImportFeature bestFeature = null;
        for (ImportFeature feature : features) {

            double nameSimilarity = scoreName(entity, feature);
            double geoScore = scoreOverlap(entity, feature);

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
     * Finds the admin entity that best matches the 
     * given the feature.
     * 
     * @param feature
     * @param nameWeight 
     * @return
     */
    private AdminEntity findBestMatchingParentEntity(ImportFeature feature, double nameWeight) {
        double bestScore = 0;
        AdminEntity bestEntity = null;
        for (AdminEntity entity : entities) {
            double score = scorePotentialParent(entity, feature, nameWeight);
            if (score > bestScore) {
                bestScore = score;
                bestEntity = entity;
            }
        }
        return bestEntity;
    }

    public double scorePotentialParent(AdminEntity entity, ImportFeature feature, double nameWeight) {
        // calculate the proportion of this feature that is contained by the
    	// potential parent
        double geoScore = scoreContainment(feature, entity);

        // calculate the name overlap
        double nameSimilarity = scoreName(entity, feature);

        // give a bonus for perfect name match
        if (nameSimilarity == 1.0) {
            nameSimilarity += 0.25;
        }
               
        // bonus for being perfectly contained
        if(geoScore > 0.99) {
        	geoScore += .50;
        }

        System.out.println(String.format("%s <> %s %.2f %.2f", entity.getName(),
            feature.toString(),
            geoScore, nameSimilarity));

        return (nameSimilarity * nameWeight) + geoScore;
    }

    /**
     * Calculates the area of intersection of the MBR's of the admin entity and
     * the feature to import
     */
    public static double areaOfIntersection(AdminEntity entity, ImportFeature feature) {
        if (entity.getBounds() == null) {
            return 0;
        }
        Envelope unitEnvelope = GeoUtils.toEnvelope(entity.getBounds());
        Envelope featureEnvelope = feature.getEnvelope();
        return unitEnvelope.intersection(featureEnvelope).getArea();
    }
    
    public static double scoreOverlap(AdminEntity entity, ImportFeature feature) {
    	if(entity.getBounds() == null) {
    		return 0;
    	}
    	return areaOfIntersection(entity, feature) / GeoUtils.toEnvelope(entity.getBounds()).getArea();
    }
    
    public static double scoreContainment(ImportFeature feature, AdminEntity entity) {
    	return areaOfIntersection(entity, feature) / feature.getEnvelope().getArea();
    }

    public static double scoreName(AdminEntity entity, ImportFeature feature) {
        return feature.similarity(entity.getName());
    }

}
