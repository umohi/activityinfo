package org.activityinfo.server.database.migration;

import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.StoreLoader;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import org.activityinfo.server.database.hibernate.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;

import static com.hp.hpl.jena.graph.NodeFactory.createLiteral;
import static org.activityinfo.graph.shared.ActivityInfoNamespace.*;


public class GraphLoader {

    private final EntityManager em;
    private final OntModel om;
    private final ObjectProperty subDivProperty;
    private final Store store;
    private StoreLoader loader;

    public GraphLoader(EntityManager em, Store store, OntModel om) {
        this.em = em;
        this.store = store;
        this.loader = store.getLoader();

        this.om = om;
        subDivProperty = this.om.getObjectProperty(SUB_DIVISION_OF);

    }
    
    public void load() {
       // loadCountryTable();
       // loadAdminLevelTable();
       // loadAdminEntity();

    }

    public void loadLocationTypes() {
        
        Map<String, String> classMap = Maps.newHashMap();
        classMap.put("Localité", POPULATED_PLACE);
        classMap.put("Ecole Primaire", SCHOOL);
        classMap.put("Centre de Santé", HEALTH_FACILITY);
        classMap.put("Village", POPULATED_PLACE);
        classMap.put("Village_ANCIENT", POPULATED_PLACE);
        classMap.put("Structure Sanitaire", HEALTH_FACILITY);
        classMap.put("Ecole", SCHOOL);
        classMap.put("Formation Sanitaire", HEALTH_FACILITY);
        classMap.put("Water Point", WATER_POINT);
        classMap.put("Point d'eau", WATER_POINT);
        classMap.put("School", SCHOOL);
        classMap.put("Health Center", HEALTH_FACILITY);

        for(LocationType type : em.createQuery("select t from LocationType t", LocationType.class).getResultList()) {
            if(type.getBoundAdminLevel() == null) {
                OntClass typeClass = om.createClass(uri(type));
                typeClass.setLabel(type.getName(), "en");
                if(classMap.containsKey(type.getName())) {
                    typeClass.setSuperClass(om.getOntClass(classMap.get(type.getName())));
                } else {
                    typeClass.setSuperClass(om.getOntClass(LOCATION));
                }
            }
        }
    }

    public void loadLocations() {

        List<Tuple> resultList = em.createQuery(
                "select loc.id, loc.locationType.id, loc.name, loc.axe, loc.x, loc.y from" +
                        " Location loc where loc.locationType.boundAdminLevel is null", Tuple.class)
                .getResultList();

        StoreLoader loader = this.loader;

        Node type = createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Node label = createURI("http://www.w3.org/2000/01/rdf-schema#label");


        loader.startBulkUpdate();

        for(Tuple tuple : resultList) {

            Node locationUri =  createURI(uri(Location.class, tuple.get(0, Integer.class)));
            Node locationClassUri = createURI(uri(LocationType.class, tuple.get(1, Integer.class)));

            loader.addTriple(new Triple(locationUri, type, locationClassUri));
            loader.addTriple(new Triple(locationUri, label, createLiteral(tuple.get(2, String.class))));
        }

        loader.finishBulkUpdate();
    }

    private void loadAdminEntity() {
        for(AdminEntity adminEntity : em.createQuery("select e from AdminEntity e", AdminEntity.class).getResultList()) {
            if(!adminEntity.isDeleted()) {
                Individual entity = om.createIndividual(uri(adminEntity), om.getOntClass(ADMIN_ENTITY));
                entity.setLabel(adminEntity.getName(), "en");

                try {
                    if(adminEntity.getParent() == null) {
                        entity.setPropertyValue(subDivProperty,
                                om.createResource(uri(adminEntity.getLevel().getCountry())));
                    } else {
                        entity.setPropertyValue(subDivProperty,
                                om.createResource(uri(adminEntity.getParent())));
                    }
                } catch(Exception e) {
                    System.err.println("WARN: " + e.getMessage());
                }
            }
        }
    }

    private void loadAdminLevelTable() {
        for(AdminLevel level : em.createQuery("select l from AdminLevel l", AdminLevel.class).getResultList()) {
            if(!level.isDeleted()) {
                System.out.println(level.getName());
                Individual levelClass = om.createIndividual(uri(level), om.getOntClass(ADMIN_LEVEL));
                levelClass.setLabel(level.getName(), "en");
                if(level.getParentId() == null) {
                    levelClass.setPropertyValue(subDivProperty,
                            om.createResource(uri(level.getCountry())));
                } else {
                    levelClass.setPropertyValue(subDivProperty,
                            om.createResource(uri(level.getParent())));
                }
            }
        }
    }

    private void loadCountryTable() {
        for(Country country : em.createQuery("select c from Country c where name != 'Global'", Country.class).getResultList()) {
            Individual countryResource = om.createIndividual(uri(country), om.getOntClass(COUNTRY));
            countryResource.setLabel(country.getName(), "en");
        }
    }

    public void loadUsersTable() {



        loader.startBulkUpdate();

        for(User user : em.createQuery("select u from User u", User.class).getResultList()) {
            Node userUri = createURI(uri(user));
            loader.addTriple(new Triple(userUri, VCARD.EMAIL.asNode(), createURI("mailto:" + user.getEmail())));
            loader.addTriple(new Triple(userUri, VCARD.FN.asNode(), createLiteral(user.getName())));

        }

        loader.finishBulkUpdate();
    }

    private Node createURI(String uri) {
        return NodeFactory.createURI(uri);
    }
    public void loadDatabases() {

        Node databaseClass = createURI(DATABASE);

        loader.startBulkUpdate();

        for(UserDatabase db : em.createQuery("select db from UserDatabase db where db.dateDeleted is null",
                UserDatabase.class).getResultList()) {
            Node dbUri = createURI(uri(db));
            loader.addTriple(new Triple(dbUri, RDF.type.asNode(), databaseClass));
            loader.addTriple(new Triple(dbUri, RDFS.label.asNode(), createLiteral(db.getName())));
            loader.addTriple(new Triple(createURI(uri(db.getOwner())), createURI(OWNS), dbUri));
        }

        loader.finishBulkUpdate();
    }

    public void loadPartnerTable() {
        StoreLoader loader = this.loader;

        loader.startBulkUpdate();

        for(Partner partner : em.createQuery("select p from Partner p", Partner.class)
                .getResultList()) {
            Node partnerUri = createURI(partner);
            loader.addTriple(new Triple(partnerUri, RDF.type.asNode(), createURI(PARTNER)));
            loader.addTriple(new Triple(partnerUri, RDFS.label.asNode(), createLiteral(partner.getName())));
        }

        loader.finishBulkUpdate();
    }

    public void loadActivityTable() {

        loader.startBulkUpdate();

        for(Activity activity : em.createQuery("select a from Activity a where dateDeleted is null", Activity.class)
                .getResultList()) {
            Node activityUri = createURI(activity);
            loader.addTriple(new Triple(activityUri, RDF.type.asNode(), OWL.Class.asNode()));
            loader.addTriple(new Triple(activityUri, RDFS.label.asNode(), createLiteral(activity.getName())));
            loader.addTriple(new Triple(createURI(activity.getDatabase()), createURI(OWNS), activityUri));
        }

        loader.finishBulkUpdate();
    }

    public void loadSites() {

        loader.startBulkUpdate();
        for(Site site : em.createQuery("select s from Site s where s.location.locationType.boundAdminLevel is null " +
                " and dateDeleted is null", Site.class)
                .getResultList()) {
            Node siteUri = createURI(site);

            loader.addTriple(new Triple(siteUri, RDF.type.asNode(), createURI(site.getActivity())));
            loader.addTriple(new Triple(siteUri, createURI(LOCATED_AT), createURI(site.getLocation())));
        }
        loader.finishBulkUpdate();
    }


    public void loadReportingPeriods() {

        loader.startBulkUpdate();
        for(Tuple rp : em.createQuery("select rp.id, rp.site.id from ReportingPeriod", Tuple.class)
                .getResultList()) {
            int rpId = rp.get(0, Integer.class);
            int siteId = rp.get(1, Integer.class);

            loader.addTriple(new Triple(createURI(uri(ReportingPeriod.class, rpId)), createURI(REPORTS_ON),
                    createURI(uri(Site.class, rpId))));
            // todo dates
        }
        loader.finishBulkUpdate();
    }

    public void loadIndicatorValues() {

        loader.startBulkUpdate();
        for(IndicatorValue value : em.createQuery("select iv from IndicatorValues iv " +
                " and dateDeleted is null", IndicatorValue.class)
                .getResultList()) {
            if(value.getValue() != null) {
                Node indicatorUri = createURI(value.getIndicator());
                Node rpUri = createURI(value.getReportingPeriod());

                loader.addTriple(new Triple(rpUri, indicatorUri, NodeFactory.createLiteral(Double.toString(value.getValue()))));
            }
        }
        loader.finishBulkUpdate();
    }

    private Node createURI(LegacyResource partner) {
        return NodeFactory.createURI(uri(partner));
    }

}
